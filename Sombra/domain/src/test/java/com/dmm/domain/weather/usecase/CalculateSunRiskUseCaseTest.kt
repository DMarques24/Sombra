package com.dmm.domain.weather.usecase

import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.weather.model.HourRange
import com.dmm.domain.weather.model.HourlyUv
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CalculateSunRiskUseCaseTest {

    private val calculate = CalculateSunRiskUseCase()
    private val day = LocalDate.of(2026, 7, 20)

    private fun hourly(vararg uvByHour: Pair<Int, Double>) =
        uvByHour.map { (hour, uv) -> HourlyUv(day.atTime(hour, 0), uv) }

    @Test
    fun `pele tipo II com UV 8 queima em cerca de 21 minutos`() {
        val risk = calculate(SkinType.II, uvNow = 8.0, hourlyToday = emptyList())

        assertEquals(21, risk.burnMinutes)
        assertEquals(50, risk.recommendedSpf)
    }

    @Test
    fun `pele mais escura aguenta mais tempo`() {
        val typeII = calculate(SkinType.II, 8.0, emptyList()).burnMinutes!!
        val typeV = calculate(SkinType.V, 8.0, emptyList()).burnMinutes!!

        assertTrue(typeV > typeII)
    }

    @Test
    fun `sem UV não há tempo para queimar`() {
        assertNull(calculate(SkinType.I, uvNow = 0.4, hourlyToday = emptyList()).burnMinutes)
    }

    @Test
    fun `as horas de sombra vão da primeira à última hora de UV alto`() {
        val risk = calculate(
            SkinType.III,
            uvNow = 4.0,
            hourlyToday = hourly(10 to 4.0, 11 to 5.5, 12 to 6.2, 13 to 8.0, 14 to 7.0, 15 to 6.0, 16 to 4.5, 17 to 2.0),
        )

        assertEquals(HourRange(12, 16), risk.shadeHours)
        assertEquals(HourRange(10, 17), risk.protectHours)
        assertEquals(50, risk.recommendedSpf)
    }

    @Test
    fun `dia de UV baixo não tem horas de sombra`() {
        val risk = calculate(SkinType.IV, uvNow = 1.0, hourlyToday = hourly(12 to 2.0, 13 to 2.4))

        assertNull(risk.shadeHours)
        assertNull(risk.protectHours)
        assertEquals(30, risk.recommendedSpf)
    }
}
