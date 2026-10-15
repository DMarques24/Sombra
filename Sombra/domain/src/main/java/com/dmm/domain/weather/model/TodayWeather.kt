package com.dmm.domain.weather.model

import java.time.Instant
import java.time.ZoneOffset

// O que o Início precisa: o tempo agora e o UV de cada hora de hoje
data class TodayWeather(
    val current: CurrentWeather,
    val hourly: List<HourlyUv>,
    val maxUv: Double,
    val fetchedAt: Instant,
    val isFromCache: Boolean,
    val zone: ZoneOffset,                   // fuso do sítio, para saber que horas são lá
)
