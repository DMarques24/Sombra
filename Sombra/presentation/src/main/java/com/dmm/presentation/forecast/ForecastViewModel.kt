package com.dmm.presentation.forecast

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.model.DataError
import com.dmm.domain.model.DomainResult
import com.dmm.domain.weather.model.DailyForecast
import com.dmm.domain.weather.usecase.GetDailyForecastUseCase
import com.dmm.domain.weather.usecase.GetHourlyUvUseCase
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
class ForecastViewModel @RequiresApi(Build.VERSION_CODES.O)
@Inject constructor(
    getHourlyUv: GetHourlyUvUseCase,
    getDailyForecastUseCase: GetDailyForecastUseCase,
    clock: Clock,
) : ViewModel() {

    // Cada vez que muda, o pedido à API é feito outra vez (botão "Tenta outra vez")
    private val retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<ForecastUiState> =
        retryTrigger
            .flatMapLatest {
                flow<ForecastUiState> {
                    val now = LocalDateTime.now(clock)
                    // Se algum dos pedidos falhar, mostra o erro e pára aqui
                    val hours = when (val result = getHourlyUv(LATITUDE, LONGITUDE, now.toLocalDate())) {
                        is DomainResult.Success -> result.data
                        is DomainResult.Failure -> return@flow emit(ForecastUiState.Error(result.error))
                    }
                    val days = when (val result = getDailyForecastUseCase(LATITUDE, LONGITUDE, now.toLocalDate())) {
                        is DomainResult.Success -> result.data
                        is DomainResult.Failure -> return@flow emit(ForecastUiState.Error(result.error))
                    }
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
    @RequiresApi(Build.VERSION_CODES.O)
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
        // TODO: trocar pela localização do utilizador. Por agora fica Coimbra
        const val LATITUDE = 40.2033
        const val LONGITUDE = -8.4103

        // O gráfico só mostra as horas com sol (8h às 19h)
        val CHART_HOURS = 8..19

        @RequiresApi(Build.VERSION_CODES.O)
        val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE", Locale("pt", "PT"))
    }
}
