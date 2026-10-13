package com.dmm.data.mapper

import com.dmm.data.remote.dto.ForecastResponseDto
import com.dmm.domain.weather.model.WeatherCondition
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

// Usa a mesma resposta real da Open-Meteo que o OpenMeteoApiTest
class ForecastMapperTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val dto = json.decodeFromString<ForecastResponseDto>(
        javaClass.classLoader!!.getResource("forecast_coimbra.json")!!.readText()
    )
    private val fetchedAt = Instant.parse("2026-10-13T17:48:00Z")

    @Test
    fun `converte o tempo atual`() {
        val current = dto.toDomain(fetchedAt).current

        assertEquals(20.9, current.temperatureC, 0.0)
        assertEquals(22.1, current.feelsLikeC, 0.0)
        assertTrue(current.isDay)
        assertEquals(WeatherCondition.Clear, current.condition)
        assertEquals(0.15, current.uvIndex, 0.0)
    }

    @Test
    fun `usa o UV da hora quando o current nao traz uv_index`() {
        val forecast = dto.copy(current = dto.current.copy(uvIndex = null)).toDomain(fetchedAt)
        // current.time é 18:45 -> procura a hora das 18:00 na lista horária
        val uvAs18 = forecast.hourly.first { it.time.hour == 18 && it.time.toLocalDate() == LocalDate.of(2026, 10, 13) }

        assertEquals(uvAs18.uvIndex, forecast.current.uvIndex, 0.0)
    }

    @Test
    fun `junta as listas horarias e diarias`() {
        val forecast = dto.toDomain(fetchedAt)

        assertEquals(120, forecast.hourly.size)
        assertEquals(5, forecast.daily.size)
        val hoje = forecast.daily.first()
        assertEquals(LocalDate.of(2026, 10, 13), hoje.date)
        assertEquals(dto.daily.temperatureMax[0], hoje.maxC, 0.0)
        assertEquals(LocalDate.parse(dto.daily.sunset[0].take(10)), hoje.date)   // o pôr do sol é do mesmo dia
        assertEquals(fetchedAt, forecast.fetchedAt)
    }
}
