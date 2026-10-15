package com.dmm.presentation.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.common.DataError
import com.dmm.domain.common.DomainResult
import com.dmm.domain.location.usecase.ResolveLocationUseCase
import com.dmm.domain.profile.model.SunPlace
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.weather.model.DailyForecast
import com.dmm.domain.weather.model.UvForecast
import com.dmm.domain.weather.usecase.CalculateSunRiskUseCase
import com.dmm.domain.weather.usecase.GetUvForecastUseCase
import com.dmm.presentation.forecast.component.HourlyUv
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getUvForecast: GetUvForecastUseCase,
    private val resolveLocation: ResolveLocationUseCase,
    private val calculateSunRisk: CalculateSunRiskUseCase,
    private val userRepository: UserRepository,
    private val clock: Clock,
) : ViewModel() {

    // Cada vez que muda, o pedido à API é feito outra vez (botão "Tenta outra vez")
    private val retryTrigger = MutableStateFlow(0)

    // O dia escolhido no seletor; null = hoje
    private val selectedDate = MutableStateFlow<LocalDate?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ForecastUiState> =
        retryTrigger
            .flatMapLatest {
                flow<ForecastUiState> {
                    // A permissão é pedida no Início; aqui só se usa (senão a cidade do registo ou Coimbra)
                    val (latitude, longitude) = resolveLocation().coordinates
                    // Se o pedido falhar, mostra o erro e pára aqui
                    val forecast = when (val result = getUvForecast(latitude, longitude)) {
                        is DomainResult.Success -> result.data
                        is DomainResult.Failure -> return@flow emit(ForecastUiState.Error(result.error))
                    }
                    // Trocar de dia ou mudar o perfil só refaz as contas, sem novo pedido
                    emitAll(
                        combine(selectedDate, userRepository.observeProfile()) { date, profile ->
                            forecast.toContent(date, profile)
                        }
                    )
                }
                    .onStart { emit(ForecastUiState.Loading) }
                    // Rede de segurança: os erros da API já chegam como DomainResult.Failure
                    .catch { emit(ForecastUiState.Error(DataError.Unknown)) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ForecastUiState.Loading)

    fun onRetry() {
        retryTrigger.value++
    }

    // Escolher o primeiro dia da lista (hoje) volta ao "hoje" automático
    fun onSelectDay(date: LocalDate) {
        val today = (uiState.value as? ForecastUiState.Content)?.dayOptions?.firstOrNull()?.date
        selectedDate.value = date.takeIf { it != today }
    }

    private fun UvForecast.toContent(date: LocalDate?, profile: UserProfile): ForecastUiState.Content {
        // A hora no sítio do tempo (as horas da previsão são as de lá)
        val now = LocalDateTime.ofInstant(clock.instant(), zone)
        val today = now.toLocalDate()
        val day = date ?: today
        val hours = hourlyOn(day)
        val isToday = day == today
        val options = daily.map { it.date }.filter { !it.isBefore(today) }.map { ForecastDayOption(it, it.label(today)) }

        return ForecastUiState.Content(
            hourly = hours
                .filter { it.time.hour in CHART_HOURS }
                .map { HourlyUv(hour = it.time.hour, uvIndex = it.uvIndex.roundToInt()) },
            currentHour = now.hour.takeIf { isToday },
            currentUv = if (isToday) {
                hours.firstOrNull { it.time.hour == now.hour }?.uvIndex?.roundToInt() ?: 0
            } else {
                hours.maxOfOrNull { it.uvIndex }?.roundToInt() ?: 0
            },
            selectedDay = options.firstOrNull { it.date == day } ?: ForecastDayOption(day, day.label(today)),
            dayOptions = options,
            bestTime = BestTimeUiState(
                forRunning = SunPlace.Running in profile.sunPlaces,
                uvHours = calculateSunRisk(profile.skinTypeOrDefault, uvNow = 0.0, hourlyToday = hours).protectHours,
            ),
            days = daysAfter(day, count = 3).map { it.toDayUiState(today) },
            isOffline = isFromCache,
        )
    }

    private fun DailyForecast.toDayUiState(today: LocalDate) = ForecastDayUiState(
        label = date.label(today),
        uvIndex = uvIndexMax.roundToInt(),
        condition = condition,
    )

    // Hoje e amanhã têm nome; os outros dias são o dia da semana (o texto é escolhido no ecrã)
    private fun LocalDate.label(today: LocalDate): DayLabel = when (this) {
        today -> DayLabel.Today
        today.plusDays(1) -> DayLabel.Tomorrow
        else -> DayLabel.Weekday(this)
    }

    private companion object {
        // O gráfico só mostra as horas com sol (8h às 19h)
        val CHART_HOURS = 8..19
    }
}
