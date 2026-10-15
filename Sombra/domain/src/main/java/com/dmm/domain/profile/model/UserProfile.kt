package com.dmm.domain.profile.model

// O que a pessoa contou sobre si no registo. Sem autenticação: fica só no telemóvel.
data class UserProfile(
    val name: String = "",
    val email: String = "",
    val city: String = "",
    val skinType: SkinType? = null,             // null = saltou o passo do tipo de pele
    val sunPlaces: Set<SunPlace> = emptySet(),
    val onboardingCompleted: Boolean = false,
) {
    // Sem tipo de pele escolhido, faz as contas para o tipo II (o mais comum em Portugal e mais cauteloso)
    val skinTypeOrDefault: SkinType get() = skinType ?: SkinType.II
}
