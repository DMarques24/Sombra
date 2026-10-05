package com.dmm.presentation.skintype

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.dmm.core.designsystem.SombraColors
import com.dmm.presentation.R

// Os 6 fototipos de Fitzpatrick (I a VI): numeração romana, a frase curta e a cor da amostra
enum class SkinType(val roman: String, @param:StringRes val description: Int, val color: Color) {
    I("I", R.string.skin_type_1, SombraColors.Skin[0]),
    II("II", R.string.skin_type_2, SombraColors.Skin[1]),
    III("III", R.string.skin_type_3, SombraColors.Skin[2]),
    IV("IV", R.string.skin_type_4, SombraColors.Skin[3]),
    V("V", R.string.skin_type_5, SombraColors.Skin[4]),
    VI("VI", R.string.skin_type_6, SombraColors.Skin[5]),
}

enum class SunPlace(@param:StringRes val label: Int) {
    BEACH(R.string.sun_place_beach),
    OUTDOOR_WORK(R.string.sun_place_outdoor_work),
    RUNNING(R.string.sun_place_running),
    CITY(R.string.sun_place_city),
    MOUNTAIN(R.string.sun_place_mountain),
}

data class SkinTypeUiState(
    val selectedSkinType: SkinType? = null,
    val selectedPlaces: Set<SunPlace> = emptySet(),
    val step: Int = 2,
    val totalSteps: Int = 3,
)
