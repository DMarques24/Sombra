package com.dmm.domain.weather.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.dmm.domain.weather.model.HourlyUv
import com.dmm.domain.weather.repository.WeatherRepository
import java.time.LocalDate
import javax.inject.Inject

// A API manda 5 dias de horas seguidas; aqui ficam só as horas do dia pedido
class GetHourlyUvUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(latitude: Double, longitude: Double, date: LocalDate): List<HourlyUv> =
        repository.getForecast(latitude, longitude).hourly
            .filter { it.time.toLocalDate() == date }
}
