package com.dmm.domain.location.usecase

import com.dmm.domain.location.model.Coordinates
import com.dmm.domain.location.model.ResolvedLocation
import com.dmm.domain.location.repository.LocationRepository
import com.dmm.domain.profile.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// Decide de onde vem o tempo, por esta ordem: GPS -> cidade do registo -> Coimbra
class ResolveLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(useGps: Boolean = true): ResolvedLocation {
        if (useGps) {
            locationRepository.getCurrentLocation()?.let { coordinates ->
                return ResolvedLocation(
                    coordinates = coordinates,
                    cityName = locationRepository.getCityName(coordinates),
                    source = ResolvedLocation.Source.Gps,
                )
            }
        }

        val city = userRepository.observeProfile().first().city.trim()
        if (city.isNotEmpty()) {
            locationRepository.getCoordinates(city)?.let { coordinates ->
                return ResolvedLocation(coordinates, city, ResolvedLocation.Source.ProfileCity)
            }
        }

        return ResolvedLocation(Coordinates.Default, Coordinates.DEFAULT_CITY, ResolvedLocation.Source.Default)
    }
}
