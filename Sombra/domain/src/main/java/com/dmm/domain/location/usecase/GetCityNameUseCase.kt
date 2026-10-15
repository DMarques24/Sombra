package com.dmm.domain.location.usecase

import com.dmm.domain.location.repository.LocationRepository
import javax.inject.Inject

// O nome da cidade onde o telemóvel está agora (botão "Usar a minha localização" do registo)
class GetCityNameUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(): String? {
        val coordinates = repository.getCurrentLocation() ?: return null
        return repository.getCityName(coordinates)
    }
}
