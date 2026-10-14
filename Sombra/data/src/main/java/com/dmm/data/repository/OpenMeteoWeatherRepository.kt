package com.dmm.data.repository

import com.dmm.data.mapper.toDomain
import com.dmm.data.remote.OpenMeteoApi
import com.dmm.data.remote.safeApiCall
import com.dmm.domain.common.DomainResult
import com.dmm.domain.weather.model.Forecast
import com.dmm.domain.weather.repository.WeatherRepository
import java.time.Clock
import javax.inject.Inject

class OpenMeteoWeatherRepository @Inject constructor(
    private val api: OpenMeteoApi,
    private val clock: Clock,
) : WeatherRepository {

    override suspend fun getForecast(latitude: Double, longitude: Double): DomainResult<Forecast> =
        safeApiCall {
            api.getForecast(latitude, longitude).toDomain(fetchedAt = clock.instant())
        }
}
