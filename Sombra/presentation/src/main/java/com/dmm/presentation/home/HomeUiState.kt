package com.dmm.presentation.home

import com.dmm.domain.weather.model.UvLevel
import com.dmm.domain.weather.model.WeatherCondition

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Error : HomeUiState
    data class Content(
        val weather: HomeWeatherUiState,
        val timerCard: TimerCardUiState,
        val isOffline: Boolean = false,
        val hoursSinceUpdate: Int = 0,
        val updatedAt: String = "13:20",
        val isRefreshing: Boolean = false,
    ) : HomeUiState
}

// O tempo de agora, já arredondado para o ecrã
data class HomeWeatherUiState(
    val temperature: Int,
    val feelsLike: Int,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val uvIndex: Int,
    val uvLevel: UvLevel,
)

sealed interface TimerCardUiState {
    data object Idle : TimerCardUiState
    data class Running(
        val remainingHours: Int,
        val remainingMinutes: Int,
        val spf: Int,
        val appliedAt: String,
        val progress: Float,
    ) : TimerCardUiState

    data class Finished(val overdueMinutes: Int, val uvNow: Int) : TimerCardUiState
}
