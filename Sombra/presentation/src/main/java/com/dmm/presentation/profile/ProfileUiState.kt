package com.dmm.presentation.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dmm.presentation.R

// Um patch da coleção. O nome e o ícone são fixos (vêm dos recursos); só "unlocked" muda por utilizador.
data class PatchUi(
    @param:StringRes val name: Int,
    @param:DrawableRes val icon: Int,
    val unlocked: Boolean,
)

// Os valores por defeito são os do design, até haver dados a sério
data class ProfileUiState(
    val name: String = "Diogo",
    val skinType: String = "II",
    val city: String = "Coimbra",
    val level: Int = 3,
    @param:StringRes val levelName: Int = R.string.level_hat,
    @param:StringRes val nextLevelName: Int = R.string.level_parasol,
    @param:StringRes val nextLevelUnlock: Int = R.string.level_parasol_unlock,
    val xp: Int = 640,
    val xpGoal: Int = 1000,
    val streakDays: Int = 12,
    val highUvDaysProtected: Int = 4,
    val highUvDays: Int = 5,
    val onTimeReapplications: Int = 38,
    val totalPatches: Int = 12,
    val patches: List<PatchUi> = listOf(
        PatchUi(R.string.patch_first_sunscreen, R.drawable.patch_primeiro_protetor, unlocked = true),
        PatchUi(R.string.patch_spf_week, R.drawable.patch_semana_spf, unlocked = true),
        PatchUi(R.string.patch_reapplier, R.drawable.patch_reaplicador, unlocked = true),
        PatchUi(R.string.patch_shade_hour, R.drawable.patch_hora_da_sombra, unlocked = true),
        PatchUi(R.string.patch_hat_on_top, R.drawable.patch_chapeu_no_topo, unlocked = true),
        PatchUi(R.string.patch_extreme_uv, R.drawable.patch_uv_extremo, unlocked = false),
        PatchUi(R.string.patch_protected_summer, R.drawable.patch_verao_limpo, unlocked = false),
        PatchUi(R.string.patch_family, R.drawable.patch_familia, unlocked = false),
    ),
) {
    val unlockedPatches: Int get() = patches.count { it.unlocked }
}
