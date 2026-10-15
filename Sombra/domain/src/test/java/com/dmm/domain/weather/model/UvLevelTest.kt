package com.dmm.domain.weather.model

import org.junit.Assert.assertEquals
import org.junit.Test

// Escala da OMS, com o valor arredondado: os limites são os casos que mais facilmente falham
class UvLevelTest {

    @Test
    fun `limites entre níveis`() {
        val cases = mapOf(
            0.0 to UvLevel.Low,
            2.4 to UvLevel.Low,
            2.5 to UvLevel.Moderate,
            5.4 to UvLevel.Moderate,
            5.5 to UvLevel.High,
            7.4 to UvLevel.High,
            7.5 to UvLevel.VeryHigh,
            10.4 to UvLevel.VeryHigh,
            10.5 to UvLevel.Extreme,
            14.0 to UvLevel.Extreme,
        )
        cases.forEach { (uv, level) -> assertEquals("UV $uv", level, UvLevel.from(uv)) }
    }
}
