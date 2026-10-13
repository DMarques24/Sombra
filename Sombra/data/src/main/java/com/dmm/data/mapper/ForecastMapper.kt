package com.dmm.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.dmm.data.remote.dto.CurrentDto
import com.dmm.data.remote.dto.DailyDto
import com.dmm.data.remote.dto.ForecastResponseDto
import com.dmm.data.remote.dto.HourlyDto
import com.dmm.domain.weather.model.CurrentWeather
import com.dmm.domain.weather.model.DailyForecast
import com.dmm.domain.weather.model.Forecast
import com.dmm.domain.weather.model.HourlyUv
import com.dmm.domain.weather.model.WeatherCondition
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// Converte a resposta da Open-Meteo (DTO, formato da API) no modelo do domínio.
// Vive no :data porque é o único módulo que conhece os DTOs; o :domain não sabe de onde vêm os dados.
@RequiresApi(Build.VERSION_CODES.O)
fun ForecastResponseDto.toDomain(fetchedAt: Instant): Forecast {
    val hourly = hourly.toDomain()
    return Forecast(
        current = current.toDomain(hourly),
        hourly = hourly,
        daily = daily.toDomain(),
        fetchedAt = fetchedAt,
    )
}

// Recebe as horas para ter um plano B se o uv_index não vier no "current"
@RequiresApi(Build.VERSION_CODES.O)
private fun CurrentDto.toDomain(hourly: List<HourlyUv>): CurrentWeather {
    val now = LocalDateTime.parse(time)
    val uvFromHourly = hourly
        .firstOrNull { it.time == now.truncatedTo(ChronoUnit.HOURS) }
        ?.uvIndex

    return CurrentWeather(
        temperatureC = temperature,
        feelsLikeC = apparentTemperature,
        isDay = isDay == 1,
        condition = WeatherCondition.fromWmoCode(weatherCode),
        uvIndex = uvIndex ?: uvFromHourly ?: 0.0,
    )
}

// A API manda listas paralelas: time[0] corresponde a uv_index[0], e assim por diante.
// O zip junta-as em pares, e uma hora sem valor de UV conta como 0.
@RequiresApi(Build.VERSION_CODES.O)
private fun HourlyDto.toDomain(): List<HourlyUv> =
    time.zip(uvIndex) { t, uv ->
        HourlyUv(
            time = LocalDateTime.parse(t),
            uvIndex = uv ?: 0.0,
        )
    }

// Aqui são 6 listas paralelas: percorre-se pelo índice
@RequiresApi(Build.VERSION_CODES.O)
private fun DailyDto.toDomain(): List<DailyForecast> =
    time.indices.map { i ->
        DailyForecast(
            date = LocalDate.parse(time[i]),
            condition = WeatherCondition.fromWmoCode(weatherCode[i]),
            maxC = temperatureMax[i],
            minC = temperatureMin[i],
            uvIndexMax = uvIndexMax[i] ?: 0.0,
            sunset = LocalDateTime.parse(sunset[i]).toLocalTime(),
        )
    }