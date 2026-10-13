package com.dmm.domain.weather.model

import java.time.Instant

// A previsão completa que vem da Open-Meteo, já no formato da app
data class Forecast(
    val current: CurrentWeather,
    val hourly: List<HourlyUv>,
    val daily: List<DailyForecast>,
    val fetchedAt: Instant,                 // para o "dados de há 2h"
)
