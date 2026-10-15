package com.dmm.presentation.home.mapper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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

@get:StringRes
internal val WeatherCondition.labelRes: Int
    get() = when (this) {
        WeatherCondition.Clear -> R.string.weather_clear
        WeatherCondition.PartlyCloudy -> R.string.weather_partly_cloudy
        WeatherCondition.Cloudy -> R.string.weather_cloudy
        WeatherCondition.Fog -> R.string.weather_fog
        WeatherCondition.Rain -> R.string.weather_rain
        WeatherCondition.Snow -> R.string.weather_snow
        WeatherCondition.Storm -> R.string.weather_storm
    }

// À noite, céu limpo ou pouco nublado mostra a lua
@DrawableRes
internal fun HomeWeatherUiState.imageRes(): Int = when (condition) {
    WeatherCondition.Clear -> if (isDay) R.drawable.weather_sol else R.drawable.weather_noite
    WeatherCondition.PartlyCloudy -> if (isDay) R.drawable.weather_sol_nublado else R.drawable.weather_noite
    WeatherCondition.Cloudy, WeatherCondition.Fog -> R.drawable.weather_nublado
    WeatherCondition.Rain, WeatherCondition.Snow, WeatherCondition.Storm -> R.drawable.weather_chuva
}

@get:StringRes
internal val UvLevel.labelRes: Int
    get() = when (this) {
        UvLevel.Low -> R.string.uv_name_low
        UvLevel.Moderate -> R.string.uv_name_moderate
        UvLevel.High -> R.string.uv_name_high
        UvLevel.VeryHigh -> R.string.uv_name_very_high
        UvLevel.Extreme -> R.string.uv_name_extreme
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
