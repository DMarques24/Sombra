package com.dmm.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.dmm.data.mapper.toDomain
import com.dmm.data.remote.OpenMeteoApi
import com.dmm.data.remote.safeApiCall
import com.dmm.domain.model.DomainResult
import com.dmm.domain.weather.model.Forecast
import com.dmm.domain.weather.repository.WeatherRepository
import java.time.Clock
import javax.inject.Inject

class OpenMeteoWeatherRepository @RequiresApi(Build.VERSION_CODES.O)
@Inject constructor(
    private val api: OpenMeteoApi,
    private val clock: Clock,
) : WeatherRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getForecast(latitude: Double, longitude: Double): DomainResult<Forecast> =
        safeApiCall {
            api.getForecast(latitude, longitude).toDomain(fetchedAt = clock.instant())
        }
}
