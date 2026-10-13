package com.dmm.domain.weather.model

enum class WeatherCondition {
    Clear, PartlyCloudy, Cloudy, Fog, Rain, Snow, Storm;

    companion object {
        fun fromWmoCode(code: Int): WeatherCondition = when (code) {
            0, 1 -> Clear
            2 -> PartlyCloudy
            3 -> Cloudy
            45, 48 -> Fog
            in 51..67, in 80..82 -> Rain
            in 71..77, 85, 86 -> Snow
            in 95..99 -> Storm
            else -> Cloudy
        }
    }
}