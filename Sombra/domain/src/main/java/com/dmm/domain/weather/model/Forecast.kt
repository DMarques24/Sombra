package com.dmm.domain.weather.model

import java.time.Instant
import java.time.ZoneOffset

data class Forecast(
    val current: CurrentWeather,
    val hourly: List<HourlyUv>,
    val daily: List<DailyForecast>,
    val fetchedAt: Instant,                 // para o "dados de há 2h"
    val zone: ZoneOffset,                   // fuso do sítio: "agora" lá pode não ser "agora" no telemóvel
    val isFromCache: Boolean = false,       // true quando não houve rede e são os últimos dados guardados
)
