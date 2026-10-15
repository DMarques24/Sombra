package com.dmm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDto(
    val timezone: String,
    // Fuso horário do sítio: as horas da resposta são as de lá, não as do telemóvel
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int = 0,
    val current: CurrentDto,
    val hourly: HourlyDto,
    val daily: DailyDto,
)

@Serializable
data class HourlyDto(
    val time: List<String>, //não precisam de @SerialName porque o nome em Kotlin já é igual ao nome no JSON.
    @SerialName("uv_index") val uvIndex: List<Double?>,
)

@Serializable
data class DailyDto(
    val time: List<String>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min") val temperatureMin: List<Double>,
    @SerialName("uv_index_max") val uvIndexMax: List<Double?>,
    val sunset: List<String>,
)

@Serializable
data class CurrentDto(
    val time: String,
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
    @SerialName("is_day") val isDay: Int,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("uv_index") val uvIndex: Double? = null,
)