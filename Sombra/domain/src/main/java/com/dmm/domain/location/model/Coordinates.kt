package com.dmm.domain.location.model

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        // Usada quando não há permissão ou o GPS não responde
        // TODO: trocar pela cidade que a pessoa escolheu no registo
        val Default = Coordinates(latitude = 40.2033, longitude = -8.4103)   // Coimbra
    }
}
