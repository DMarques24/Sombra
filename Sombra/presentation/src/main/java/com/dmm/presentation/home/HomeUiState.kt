package com.dmm.presentation.home

import com.dmm.domain.common.DataError
import com.dmm.domain.weather.model.UvLevel
import com.dmm.domain.weather.model.WeatherCondition

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val error: DataError) : HomeUiState
    data class Content(
        val weather: HomeWeatherUiState,
        val timerCard: TimerCardUiState,
        val isOffline: Boolean = false,
        val hoursSinceUpdate: Int = 0,
        val updatedAt: String = "13:20",
        val isRefreshing: Boolean = false,
        val isDefaultLocation: Boolean = false,
        val locationPrompt: LocationPrompt = LocationPrompt.None,
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

// Cartão que pede a localização quando a pessoa recusou a permissão
enum class LocationPrompt {
    None,           // tem permissão: não mostra nada
    AskAgain,       // recusou uma vez: o botão volta a perguntar
    OpenSettings,   // "não voltar a perguntar": o botão abre as definições da app
}
