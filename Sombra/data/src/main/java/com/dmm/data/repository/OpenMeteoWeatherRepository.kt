package com.dmm.data.repository

import com.dmm.data.local.CachedForecast
import com.dmm.data.local.LocalStore
import com.dmm.data.mapper.toDomain
import com.dmm.data.remote.OpenMeteoApi
import com.dmm.data.remote.safeApiCall
import com.dmm.domain.common.DataError
import com.dmm.domain.common.DomainResult
import com.dmm.domain.weather.model.Forecast
import com.dmm.domain.weather.repository.WeatherRepository
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.math.abs

class OpenMeteoWeatherRepository @Inject constructor(
    private val api: OpenMeteoApi,
    private val store: LocalStore,
    private val clock: Clock,
) : WeatherRepository {

    override suspend fun getForecast(latitude: Double, longitude: Double): DomainResult<Forecast> {
        val result = safeApiCall {
            val response = api.getForecast(latitude, longitude)
            val fetchedAt = clock.instant()
            store.write(
                KEY,
                CachedForecast.serializer(),
                CachedForecast(latitude.coarse(), longitude.coarse(), fetchedAt.toEpochMilli(), response),
            )
            response.toDomain(fetchedAt = fetchedAt)
        }
        // Sem internet: mostra os últimos dados guardados, se forem deste sítio e recentes
        if (result is DomainResult.Failure && result.error == DataError.NoConnection) {
            cachedForecast(latitude, longitude)?.let { return DomainResult.Success(it) }
        }
        return result
    }

    private fun cachedForecast(latitude: Double, longitude: Double): Forecast? {
        val cached = store.read(KEY, CachedForecast.serializer()) ?: return null
        val fetchedAt = Instant.ofEpochMilli(cached.fetchedAtMillis)
        val isNearby = abs(cached.latitude - latitude) < MAX_DISTANCE_DEGREES &&
            abs(cached.longitude - longitude) < MAX_DISTANCE_DEGREES
        val isRecent = Duration.between(fetchedAt, clock.instant()) < MAX_AGE
        if (!isNearby || !isRecent) return null
        return cached.response.toDomain(fetchedAt = fetchedAt).copy(isFromCache = true)
    }

    // A cache só precisa de saber se é "do mesmo sítio" (0,5° de margem): guarda-se a localização
    // com 1 casa decimal (~11 km), para não ficar uma posição precisa no telemóvel nem nas cópias de segurança
    private fun Double.coarse(): Double = Math.round(this * 10) / 10.0

    private companion object {
        const val KEY = "forecast_cache"
        const val MAX_DISTANCE_DEGREES = 0.5           // ~50 km
        val MAX_AGE: Duration = Duration.ofDays(2)
    }
}
