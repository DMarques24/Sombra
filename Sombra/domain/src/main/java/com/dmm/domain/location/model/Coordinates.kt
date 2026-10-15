package com.dmm.domain.location.model

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        // Usada quando não há permissão, o GPS não responde e a cidade do registo não foi encontrada
        val Default = Coordinates(latitude = 40.2033, longitude = -8.4103)
        const val DEFAULT_CITY = "Coimbra"
    }
}
