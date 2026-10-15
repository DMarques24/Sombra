package com.dmm.presentation.forecast

import com.dmm.domain.common.DataError
import com.dmm.domain.weather.model.HourRange
import com.dmm.domain.weather.model.WeatherCondition
import com.dmm.presentation.forecast.component.HourlyUv
import java.time.LocalDate

sealed interface ForecastUiState {
    data object Loading : ForecastUiState
    data class Error(val error: DataError) : ForecastUiState
    data class Content(
        val hourly: List<HourlyUv>,
        val currentHour: Int?,                  // null quando o dia escolhido não é hoje
        val currentUv: Int,                     // hoje: o UV agora; outros dias: o máximo
        val selectedDay: ForecastDayOption,
        val dayOptions: List<ForecastDayOption>,
        val bestTime: BestTimeUiState,
        val days: List<ForecastDayUiState>,
        val isOffline: Boolean = false,
    ) : ForecastUiState {
        val isToday: Boolean get() = currentHour != null
    }
}

// Como se chama um dia no ecrã. A ViewModel só diz qual é; o texto ("Hoje", "Sáb")
// é escolhido no composable, com as strings e a língua do telemóvel
sealed interface DayLabel {
    data object Today : DayLabel
    data object Tomorrow : DayLabel
    data class Weekday(val date: LocalDate) : DayLabel
}

// Uma linha da lista dos próximos dias: "Amanhã", "Sáb", "Dom"
data class ForecastDayUiState(
    val label: DayLabel,
    val uvIndex: Int,
    val condition: WeatherCondition,
)

// Uma opção do seletor de dia ("Hoje", "Amanhã", "Sáb"...)
data class ForecastDayOption(val date: LocalDate, val label: DayLabel)

// "Melhor hora para correr": antes e depois das horas de UV 3 ou mais
data class BestTimeUiState(
    val forRunning: Boolean,                    // a pessoa disse que corre: o título fala de correr
    val uvHours: HourRange?,                    // null: o UV fica baixo o dia todo
)
