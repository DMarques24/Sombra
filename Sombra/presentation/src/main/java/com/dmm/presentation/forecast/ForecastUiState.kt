package com.dmm.presentation.forecast

import com.dmm.domain.model.DataError
import com.dmm.domain.weather.model.WeatherCondition
import com.dmm.presentation.forecast.component.HourlyUv

sealed interface ForecastUiState {
    data object Loading : ForecastUiState
    data class Error(val error: DataError) : ForecastUiState
    data class Content(
        val hourly: List<HourlyUv>,
        val currentHour: Int,
        val currentUv: Int,
        val days: List<ForecastDayUiState>,
    ) : ForecastUiState
}

// Uma linha da lista dos próximos dias: "Amanhã", "Sáb", "Dom"
data class ForecastDayUiState(
    val label: String,
    val uvIndex: Int,
    val condition: WeatherCondition,
)
