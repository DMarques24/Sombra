package com.dmm.domain.progress.usecase

import com.dmm.domain.progress.model.Level
import com.dmm.domain.progress.model.Patch
import com.dmm.domain.progress.model.UserProgress
import com.dmm.domain.progress.repository.UvHistoryRepository
import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Clock
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import javax.inject.Inject

class ObserveProgressUseCase @Inject constructor(
    private val sunscreenRepository: SunscreenRepository,
    private val uvHistoryRepository: UvHistoryRepository,
    private val clock: Clock,
) {
    operator fun invoke(): Flow<UserProgress> =
        combine(sunscreenRepository.observeHistory(), uvHistoryRepository.observeDailyMaxUv()) { events, uvDays ->
            calculateProgress(events, uvDays, today = LocalDate.now(clock), zone = clock.zone)
        }
}

private const val HIGH_UV = 6
private const val HIGH_UV_PERIOD_DAYS = 30L

// Função pura (sem repositórios nem relógio) para ser fácil de testar
internal fun calculateProgress(
    events: List<SunscreenEvent>,
    uvDays: Map<LocalDate, Double>,
    today: LocalDate,
    zone: ZoneId,
): UserProgress {
    val xp = events.sumOf { it.xp }
    val level = Level.fromXp(xp)
    val applications = events.filter { it.isApplication }
    val protectedDays = applications.map { it.at.atZone(zone).toLocalDate() }.toSortedSet()
    val onTime = events.count { it.type == SunscreenEvent.Type.ReappliedOnTime }

    // Dias de UV alto: os que a previsão disse, mais os dias em que se aplicou protetor com UV alto
    val periodStart = today.minusDays(HIGH_UV_PERIOD_DAYS - 1)
    val highUvDays = (
        uvDays.filterValues { it >= HIGH_UV }.keys +
            applications.filter { it.uvIndex >= HIGH_UV }.map { it.at.atZone(zone).toLocalDate() }
        ).filter { !it.isBefore(periodStart) && !it.isAfter(today) }.toSet()

    val patches = buildSet {
        if (applications.isNotEmpty()) add(Patch.FirstSunscreen)
        if (longestStreak(protectedDays) >= 7) add(Patch.SpfWeek)
        if (onTime >= 10) add(Patch.Reapplier)
        if (events.any { it.type == SunscreenEvent.Type.LeftSun && it.at.atZone(zone).hour in 12..15 }) {
            add(Patch.ShadeHour)
        }
        if (level >= Level.Hat) add(Patch.HatOnTop)
        if (applications.any { it.uvIndex >= 11 }) add(Patch.ExtremeUv)
        if (protectedDays.count { it.month in SUMMER } >= 30) add(Patch.ProtectedSummer)
        if (level >= Level.Parasol) add(Patch.Family)
    }

    return UserProgress(
        xp = xp,
        level = level,
        streakDays = currentStreak(protectedDays, today),
        highUvDaysProtected = highUvDays.count { it in protectedDays },
        highUvDays = highUvDays.size,
        onTimeReapplications = onTime,
        unlockedPatches = patches,
    )
}

private val SUMMER = setOf(Month.JUNE, Month.JULY, Month.AUGUST)

// Dias seguidos com protetor até hoje. Se hoje ainda não aplicou, a sequência de ontem ainda conta.
private fun currentStreak(days: Set<LocalDate>, today: LocalDate): Int {
    var day = if (today in days) today else today.minusDays(1)
    var streak = 0
    while (day in days) {
        streak++
        day = day.minusDays(1)
    }
    return streak
}

private fun longestStreak(sortedDays: Set<LocalDate>): Int {
    var longest = 0
    var current = 0
    var previous: LocalDate? = null
    for (day in sortedDays) {
        current = if (previous != null && previous.plusDays(1) == day) current + 1 else 1
        longest = maxOf(longest, current)
        previous = day
    }
    return longest
}
