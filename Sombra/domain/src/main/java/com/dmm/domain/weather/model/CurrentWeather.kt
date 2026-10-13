package com.dmm.domain.weather.model

// O tempo agora
data class CurrentWeather(
    val temperatureC: Double,
    val feelsLikeC: Double,
    val isDay: Boolean,                     // a API manda 1/0, aqui fica true/false
    val condition: WeatherCondition,        // a API manda 0, 3, 61..., aqui fica um enum
    val uvIndex: Double,
) {
    val uvLevel: UvLevel get() = UvLevel.from(uvIndex)
}
