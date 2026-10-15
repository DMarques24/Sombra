package com.dmm.domain.progress.usecase

import com.dmm.domain.progress.model.Level
import com.dmm.domain.progress.model.Patch
import com.dmm.domain.sunscreen.model.SunscreenEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class CalculateProgressTest {

    private val zone = ZoneOffset.UTC
    private val today = LocalDate.of(2026, 7, 20)

    private fun event(
        type: SunscreenEvent.Type,
        date: LocalDate,
        hour: Int = 10,
        uv: Int = 5,
        xp: Int = 10,
    ) = SunscreenEvent(type, date.atTime(LocalTime.of(hour, 0)).toInstant(zone), spf = 50, uvIndex = uv, xp = xp)

    @Test
    fun `sem histórico começa no primeiro nível sem patches`() {
        val progress = calculateProgress(emptyList(), emptyMap(), today, zone)

        assertEquals(0, progress.xp)
        assertEquals(Level.Sunglasses, progress.level)
        assertEquals(0, progress.streakDays)
        assertTrue(progress.unlockedPatches.isEmpty())
    }

    @Test
    fun `a sequência conta dias seguidos e não se perde antes de aplicar hoje`() {
        val events = (1L..3L).map { event(SunscreenEvent.Type.Applied, today.minusDays(it)) }

        val progress = calculateProgress(events, emptyMap(), today, zone)

        assertEquals(3, progress.streakDays)
        assertTrue(Patch.FirstSunscreen in progress.unlockedPatches)
        assertFalse(Patch.SpfWeek in progress.unlockedPatches)
    }

    @Test
    fun `um dia sem protetor parte a sequência`() {
        val events = listOf(
            event(SunscreenEvent.Type.Applied, today),
            event(SunscreenEvent.Type.Applied, today.minusDays(2)),
        )

        assertEquals(1, calculateProgress(events, emptyMap(), today, zone).streakDays)
    }

    @Test
    fun `sete dias seguidos desbloqueiam a Semana SPF`() {
        val events = (0L..6L).map { event(SunscreenEvent.Type.Applied, today.minusDays(10 + it)) }

        val progress = calculateProgress(events, emptyMap(), today, zone)

        assertTrue(Patch.SpfWeek in progress.unlockedPatches)
        assertEquals(0, progress.streakDays)
    }

    @Test
    fun `dias de UV alto protegidos usam a previsão e as aplicações`() {
        val uvDays = mapOf(
            today to 8.0,
            today.minusDays(1) to 7.0,
            today.minusDays(2) to 2.0,
            today.minusDays(40) to 9.0,         // fora dos últimos 30 dias
        )
        val events = listOf(event(SunscreenEvent.Type.Applied, today, uv = 8))

        val progress = calculateProgress(events, uvDays, today, zone)

        assertEquals(2, progress.highUvDays)
        assertEquals(1, progress.highUvDaysProtected)
    }

    @Test
    fun `XP soma os eventos e define o nível e os patches de nível`() {
        val events = List(52) { event(SunscreenEvent.Type.ReappliedOnTime, today, xp = 20) }

        val progress = calculateProgress(events, emptyMap(), today, zone)

        assertEquals(1040, progress.xp)
        assertEquals(Level.Parasol, progress.level)
        assertEquals(52, progress.onTimeReapplications)
        assertTrue(progress.unlockedPatches.containsAll(listOf(Patch.Reapplier, Patch.HatOnTop, Patch.Family)))
    }

    @Test
    fun `sair do sol ao meio-dia dá a Hora da sombra`() {
        val events = listOf(event(SunscreenEvent.Type.LeftSun, today, hour = 13, xp = 0))

        val progress = calculateProgress(events, emptyMap(), today, zone)

        assertTrue(Patch.ShadeHour in progress.unlockedPatches)
        assertFalse(Patch.FirstSunscreen in progress.unlockedPatches)
    }
}
