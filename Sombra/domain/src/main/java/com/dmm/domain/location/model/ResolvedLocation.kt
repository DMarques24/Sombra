package com.dmm.domain.location.model

// Onde a app vai buscar o tempo, e de onde veio essa localização
data class ResolvedLocation(
    val coordinates: Coordinates,
    val cityName: String?,          // null quando o GPS funcionou mas o Geocoder não sabe o nome
    val source: Source,
) {
    enum class Source {
        Gps,            // a localização do telemóvel
        ProfileCity,    // a cidade que a pessoa escreveu no registo
        Default,        // Coimbra: não houve outra hipótese
    }
}
