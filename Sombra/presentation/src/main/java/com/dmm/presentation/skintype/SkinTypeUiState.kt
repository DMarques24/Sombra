package com.dmm.presentation.skintype

import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.profile.model.SunPlace

data class SkinTypeUiState(
    val selectedSkinType: SkinType? = null,
    val selectedPlaces: Set<SunPlace> = emptySet(),
    val step: Int = 2,
    val totalSteps: Int = 3,
)
