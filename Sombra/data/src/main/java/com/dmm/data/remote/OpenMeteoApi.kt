package com.dmm.data.remote

import com.dmm.data.remote.dto.ForecastResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class OpenMeteoApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getForecast(latitude: Double, longitude: Double): ForecastResponseDto =
        client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("current", "temperature_2m,apparent_temperature,is_day,weather_code,uv_index")
            parameter("hourly", "uv_index")
            parameter(
                "daily",
                "weather_code,temperature_2m_max,temperature_2m_min,uv_index_max,sunset"
            )
            parameter("timezone", "auto")
            parameter("forecast_days", 5)
        }.body()
}