package com.dmm.domain.weather.usecase

import com.dmm.domain.common.DomainResult
import com.dmm.domain.common.map
import com.dmm.domain.weather.model.UvForecast
import com.dmm.domain.weather.repository.WeatherRepository
import java.time.LocalDate
import javax.inject.Inject

// Um só pedido à API: a Open-Meteo devolve tudo de uma vez, por isso as horas e os dias
// saem do mesmo Forecast (antes eram dois use cases e dois pedidos iguais)
class GetUvForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        days: Int = 3,
    ): DomainResult<UvForecast> =
        repository.getForecast(latitude, longitude).map { forecast ->
            UvForecast(
                // A API manda 5 dias de horas seguidas; aqui ficam só as horas do dia pedido
                hourly = forecast.hourly.filter { it.time.toLocalDate() == date },
                nextDays = forecast.daily
                    .filter { it.date.isAfter(date) }
                    .take(days),
            )
        }
}
