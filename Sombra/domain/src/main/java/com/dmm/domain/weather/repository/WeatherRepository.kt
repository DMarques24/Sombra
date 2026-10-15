package com.dmm.domain.weather.repository

import com.dmm.domain.common.DomainResult
import com.dmm.domain.weather.model.Forecast

// A Open-Meteo devolve tudo numa só chamada; cada use case tira do Forecast a parte que precisa
interface WeatherRepository {
    suspend fun getForecast(latitude: Double, longitude: Double): DomainResult<Forecast>
}
