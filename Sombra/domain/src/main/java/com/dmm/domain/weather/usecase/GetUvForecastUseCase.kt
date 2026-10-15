package com.dmm.domain.weather.usecase

import com.dmm.domain.common.DomainResult
import com.dmm.domain.common.map
import com.dmm.domain.weather.model.UvForecast
import com.dmm.domain.weather.repository.WeatherRepository
import javax.inject.Inject

// Um só pedido à API: a Open-Meteo devolve tudo de uma vez, e o ecrã escolhe o dia que quer mostrar
class GetUvForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): DomainResult<UvForecast> =
        repository.getForecast(latitude, longitude).map { forecast ->
            UvForecast(
                hourly = forecast.hourly,
                daily = forecast.daily,
                isFromCache = forecast.isFromCache,
                zone = forecast.zone,
            )
        }
}
