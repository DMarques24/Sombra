package com.dmm.domain.location.repository

import com.dmm.domain.location.model.Coordinates

interface LocationRepository {
    // null quando não há permissão ou o telemóvel não consegue saber onde está
    suspend fun getCurrentLocation(): Coordinates?
}
