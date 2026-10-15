package com.dmm.domain.weather.usecase

import com.dmm.domain.common.DomainResult
import com.dmm.domain.common.map
import com.dmm.domain.progress.repository.UvHistoryRepository
import com.dmm.domain.weather.model.TodayWeather
import com.dmm.domain.weather.repository.WeatherRepository
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class GetTodayWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository,
    private val uvHistoryRepository: UvHistoryRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): DomainResult<TodayWeather> {
        var today = LocalDate.now(clock)
        val result = repository.getForecast(latitude, longitude).map { forecast ->
            // "Hoje" é o dia no sítio do tempo (pode ser diferente do dia no telemóvel)
            today = LocalDateTime.ofInstant(clock.instant(), forecast.zone).toLocalDate()
            val hourly = forecast.hourly.filter { it.time.toLocalDate() == today }
            TodayWeather(
                current = forecast.current,
                hourly = hourly,
                maxUv = forecast.daily.firstOrNull { it.date == today }?.uvIndexMax
                    ?: hourly.maxOfOrNull { it.uvIndex }
                    ?: forecast.current.uvIndex,
                fetchedAt = forecast.fetchedAt,
                isFromCache = forecast.isFromCache,
                zone = forecast.zone,
            )
        }
        // Guarda o UV máximo de hoje para as estatísticas do Perfil ("dias de UV alto protegidos")
        if (result is DomainResult.Success) uvHistoryRepository.record(today, result.data.maxUv)
        return result
    }
}
