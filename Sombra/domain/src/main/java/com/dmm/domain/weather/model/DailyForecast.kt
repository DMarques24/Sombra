package com.dmm.domain.weather.model

import java.time.LocalDate
import java.time.LocalTime

// Resumo de um dia (para os cartões da Previsão)
data class DailyForecast(
    val date: LocalDate,
    val condition: WeatherCondition,
    val maxC: Double,
    val minC: Double,
    val uvIndexMax: Double,
    val sunset: LocalTime,                  // só interessa a hora: "19:14"
) {
    val uvLevel: UvLevel get() = UvLevel.from(uvIndexMax)
}
