package com.dmm.presentation.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.location.model.Coordinates
import com.dmm.domain.location.repository.LocationRepository
import com.dmm.domain.common.DataError
import com.dmm.domain.common.DomainResult
import com.dmm.domain.weather.model.DailyForecast
import com.dmm.domain.weather.usecase.GetUvForecastUseCase
import com.dmm.presentation.forecast.component.HourlyUv
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ForecastViewModel @Inject constructor(
    getUvForecast: GetUvForecastUseCase,
    locationRepository: LocationRepository,
    clock: Clock,
) : ViewModel() {

    // Cada vez que muda, o pedido à API é feito outra vez (botão "Tenta outra vez")
    private val retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ForecastUiState> =
        retryTrigger
            .flatMapLatest {
                flow<ForecastUiState> {
                    val now = LocalDateTime.now(clock)
                    // A permissão é pedida no Início; aqui só se usa (ou Coimbra se não houver)
                    val (latitude, longitude) = locationRepository.getCurrentLocation() ?: Coordinates.Default
                    // Se o pedido falhar, mostra o erro e pára aqui
                    val forecast = when (val result = getUvForecast(latitude, longitude, now.toLocalDate())) {
                        is DomainResult.Success -> result.data
                        is DomainResult.Failure -> return@flow emit(ForecastUiState.Error(result.error))
                    }
                    val hours = forecast.hourly
                    val days = forecast.nextDays
                    emit(
                        ForecastUiState.Content(
                            hourly = hours
                                .filter { it.time.hour in CHART_HOURS }
                                .map {
                                    HourlyUv(
                                        hour = it.time.hour,
                                        uvIndex = it.uvIndex.roundToInt()
                                    )
                                },
                            currentHour = now.hour,
                            currentUv = hours
                                .firstOrNull { it.time.hour == now.hour }
                                ?.uvIndex?.roundToInt() ?: 0,
                            days = days.map { it.toDayUiState(today = now.toLocalDate()) },
                        )
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

    // Amanhã fica "Amanhã"; os outros dias ficam abreviados: "Sáb", "Dom"
    private fun DailyForecast.toDayUiState(today: LocalDate) = ForecastDayUiState(
        label = if (date == today.plusDays(1)) {
            "Amanhã"
        } else {
            date.format(DAY_FORMAT).removeSuffix(".").replaceFirstChar { it.uppercase() }
        },
        uvIndex = uvIndexMax.roundToInt(),
        condition = condition,
    )

    private companion object {
        // O gráfico só mostra as horas com sol (8h às 19h)
        val CHART_HOURS = 8..19

        val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE", Locale("pt", "PT"))
    }
}
