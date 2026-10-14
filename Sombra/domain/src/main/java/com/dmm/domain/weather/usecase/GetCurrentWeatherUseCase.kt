package com.dmm.domain.weather.usecase

import com.dmm.domain.weather.model.CurrentWeather
import com.dmm.domain.weather.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): CurrentWeather =
        repository.getForecast(latitude, longitude).current
}
