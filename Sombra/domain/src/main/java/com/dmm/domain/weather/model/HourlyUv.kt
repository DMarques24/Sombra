package com.dmm.domain.weather.model

import java.time.LocalDateTime

// UV de uma hora (para o gráfico da Previsão)
data class HourlyUv(
    val time: LocalDateTime,
    val uvIndex: Double,
) {
    val uvLevel: UvLevel get() = UvLevel.from(uvIndex)
}
