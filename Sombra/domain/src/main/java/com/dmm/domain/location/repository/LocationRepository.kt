package com.dmm.domain.location.repository

import com.dmm.domain.location.model.Coordinates

interface LocationRepository {
    // null quando não há permissão ou o telemóvel não consegue saber onde está
    suspend fun getCurrentLocation(): Coordinates?

    // Nome da cidade nestas coordenadas ("Coimbra"); null se o Geocoder não souber
    suspend fun getCityName(coordinates: Coordinates): String?

    // O contrário: as coordenadas de uma cidade escrita pela pessoa; null se não a encontrar
    suspend fun getCoordinates(cityName: String): Coordinates?
}
