package com.dmm.domain.weather.usecase

import com.dmm.domain.common.DomainResult
import com.dmm.domain.progress.repository.UvHistoryRepository
import com.dmm.domain.weather.model.CurrentWeather
import com.dmm.domain.weather.model.Forecast
import com.dmm.domain.weather.model.HourlyUv
import com.dmm.domain.weather.model.WeatherCondition
import com.dmm.domain.weather.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class GetTodayWeatherUseCaseTest {

    // Telemóvel em Lisboa às 00:30 de dia 16; o tempo é da Califórnia, onde ainda são 16:30 de dia 15
    private val clock = Clock.fixed(Instant.parse("2026-07-15T23:30:00Z"), ZoneId.of("Europe/Lisbon"))
    private val california = ZoneOffset.ofHours(-7)

    private val hourly = (0..47).map { h ->
        HourlyUv(LocalDateTime.of(2026, 7, 15, 0, 0).plusHours(h.toLong()), uvIndex = h.toDouble())
    }

    private val repository = object : WeatherRepository {
        override suspend fun getForecast(latitude: Double, longitude: Double) = DomainResult.Success(
            Forecast(
                current = CurrentWeather(25.0, 25.0, true, WeatherCondition.Clear, 5.0),
                hourly = hourly,
                daily = emptyList(),
                fetchedAt = clock.instant(),
                zone = california,
            )
        )
    }

    private val uvHistory = object : UvHistoryRepository {
        val recorded = mutableMapOf<LocalDate, Double>()
        override fun observeDailyMaxUv(): Flow<Map<LocalDate, Double>> = emptyFlow()
        override suspend fun record(date: LocalDate, maxUv: Double) { recorded[date] = maxUv }
        override suspend fun clear() = recorded.clear()
    }

    @Test
    fun `hoje é o dia no sítio do tempo e não o dia no telemóvel`() = runTest {
        val result = GetTodayWeatherUseCase(repository, uvHistory, clock)(0.0, 0.0)

        val today = (result as DomainResult.Success).data
        assertEquals(24, today.hourly.size)
        assertEquals(LocalDate.of(2026, 7, 15), today.hourly.first().time.toLocalDate())
        assertEquals(setOf(LocalDate.of(2026, 7, 15)), uvHistory.recorded.keys)
    }
}
