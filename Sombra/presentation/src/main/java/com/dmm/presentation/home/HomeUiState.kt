package com.dmm.presentation.home

import com.dmm.domain.common.DataError
import com.dmm.domain.weather.model.HourRange
import com.dmm.domain.weather.model.UvLevel
import com.dmm.domain.weather.model.WeatherCondition

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val error: DataError) : HomeUiState
    data class Content(
        val weather: HomeWeatherUiState,
        val risk: SunRiskUiState,
        val timerCard: TimerCardUiState,
        val updatedAt: String,
        val locationName: String?,                  // null: GPS sem nome de cidade ("A tua localização")
        val isOffline: Boolean = false,
        val hoursSinceUpdate: Int = 0,
        val isRefreshing: Boolean = false,
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
    val uvUntilHour: Int? = null,                   // "Muito alto até às 16h"; null se já passou
)

// O aviso do cartão: em quanto tempo queima e quando procurar sombra
data class SunRiskUiState(
    val burnMinutes: Int?,                          // null: UV baixo, não há risco agora
    val skinType: String,
    val shadeHours: HourRange?,
    val protectHours: HourRange?,
    val recommendedSpf: Int,
) {
    // Menos de 2 horas para queimar já merece o cartão vermelho
    val isHigh: Boolean get() = burnMinutes != null && burnMinutes < BURN_MINUTES_CAP

    // A partir de 2 horas o número exato já não interessa: o ecrã mostra "120+"
    val isBeyondCap: Boolean get() = burnMinutes != null && burnMinutes >= BURN_MINUTES_CAP

    companion object {
        const val BURN_MINUTES_CAP = 120
    }
}

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
