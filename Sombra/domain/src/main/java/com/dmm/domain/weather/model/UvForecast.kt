package com.dmm.domain.weather.model

import java.time.LocalDate
import java.time.ZoneOffset

// O que o ecrã da Previsão precisa: o UV hora a hora e o resumo de cada dia.
// Vem tudo de um só pedido; trocar de dia no ecrã não volta a pedir à API.
data class UvForecast(
    val hourly: List<HourlyUv>,
    val daily: List<DailyForecast>,
    val isFromCache: Boolean,
    val zone: ZoneOffset,                   // fuso do sítio: "hoje" e "agora" são os de lá
) {
    fun hourlyOn(date: LocalDate): List<HourlyUv> = hourly.filter { it.time.toLocalDate() == date }

    fun daysAfter(date: LocalDate, count: Int): List<DailyForecast> =
        daily.filter { it.date.isAfter(date) }.take(count)
}
