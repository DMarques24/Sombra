package com.dmm.presentation.home.mapper

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.dmm.core.designsystem.SombraColors
import com.dmm.domain.weather.model.CurrentWeather
import com.dmm.domain.weather.model.UvLevel
import com.dmm.domain.weather.model.WeatherCondition
import com.dmm.presentation.R
import com.dmm.presentation.home.HomeWeatherUiState
import kotlin.math.roundToInt

// O ecrã só mostra números inteiros: 27.4° -> 27°
internal fun CurrentWeather.toHomeWeather(): HomeWeatherUiState = HomeWeatherUiState(
    temperature = temperatureC.roundToInt(),
    feelsLike = feelsLikeC.roundToInt(),
    condition = condition,
    isDay = isDay,
    uvIndex = uvIndex.roundToInt(),
    uvLevel = uvLevel,
)

internal val WeatherCondition.label: String
    get() = when (this) {
        WeatherCondition.Clear -> "Céu limpo"
        WeatherCondition.PartlyCloudy -> "Pouco nublado"
        WeatherCondition.Cloudy -> "Nublado"
        WeatherCondition.Fog -> "Nevoeiro"
        WeatherCondition.Rain -> "Chuva"
        WeatherCondition.Snow -> "Neve"
        WeatherCondition.Storm -> "Trovoada"
    }

// À noite, céu limpo ou pouco nublado mostra a lua
@DrawableRes
internal fun HomeWeatherUiState.imageRes(): Int = when (condition) {
    WeatherCondition.Clear -> if (isDay) R.drawable.weather_sol else R.drawable.weather_noite
    WeatherCondition.PartlyCloudy -> if (isDay) R.drawable.weather_sol_nublado else R.drawable.weather_noite
    WeatherCondition.Cloudy, WeatherCondition.Fog -> R.drawable.weather_nublado
    WeatherCondition.Rain, WeatherCondition.Snow, WeatherCondition.Storm -> R.drawable.weather_chuva
}

internal val UvLevel.label: String
    get() = when (this) {
        UvLevel.Low -> "Baixo"
        UvLevel.Moderate -> "Moderado"
        UvLevel.High -> "Alto"
        UvLevel.VeryHigh -> "Muito alto"
        UvLevel.Extreme -> "Extremo"
    }

internal val UvLevel.color: Color
    get() = when (this) {
        UvLevel.Low -> SombraColors.UvLow
        UvLevel.Moderate -> SombraColors.UvModerate
        UvLevel.High -> SombraColors.UvHigh
        UvLevel.VeryHigh -> SombraColors.UvVeryHigh
        UvLevel.Extreme -> SombraColors.UvExtreme
    }

// O amarelo do "Moderado" é claro demais para texto branco
internal val UvLevel.onColor: Color
    get() = if (this == UvLevel.Moderate) SombraColors.OnUvModerate else Color.White
