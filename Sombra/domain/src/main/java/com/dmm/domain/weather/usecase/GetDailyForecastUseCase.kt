package com.dmm.domain.weather.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.dmm.domain.model.DomainResult
import com.dmm.domain.model.map
import com.dmm.domain.weather.model.DailyForecast
import com.dmm.domain.weather.repository.WeatherRepository
import java.time.LocalDate
import javax.inject.Inject


class GetDailyForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        date: LocalDate,
        days: Int = 3,
    ): DomainResult<List<DailyForecast>> =
        repository.getForecast(latitude, longitude).map { forecast ->
            forecast.daily
                .filter { it.date.isAfter(date) }
                .take(days)
        }
}
