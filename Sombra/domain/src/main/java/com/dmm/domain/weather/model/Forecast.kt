package com.dmm.domain.weather.model

import java.time.Instant

data class Forecast(
    val current: CurrentWeather,
    val hourly: List<HourlyUv>,
    val daily: List<DailyForecast>,
    val fetchedAt: Instant,                 // para o "dados de há 2h"
)
