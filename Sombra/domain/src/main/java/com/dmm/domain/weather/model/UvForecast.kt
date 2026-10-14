package com.dmm.domain.weather.model

// O que o ecrã da Previsão precisa: o UV hora a hora de um dia e o resumo dos dias seguintes
data class UvForecast(
    val hourly: List<HourlyUv>,
    val nextDays: List<DailyForecast>,
)
