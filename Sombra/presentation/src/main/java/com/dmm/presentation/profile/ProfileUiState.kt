package com.dmm.presentation.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.progress.model.Patch
import com.dmm.presentation.R
import com.dmm.presentation.common.icon
import com.dmm.presentation.common.title

// Um patch da coleção. O nome e o ícone são fixos (vêm dos recursos); só "unlocked" muda por utilizador.
data class PatchUi(
    @param:StringRes val name: Int,
    @param:DrawableRes val icon: Int,
    val unlocked: Boolean,
)

// Os valores por defeito são os do design (usados na preview)
data class ProfileUiState(
    val name: String = "Diogo",
    val skinType: String? = "II",               // null: saltou o passo do tipo de pele
    val city: String = "Coimbra",
    val level: Int = 3,
    @param:StringRes val levelName: Int = R.string.level_hat,
    @param:StringRes val nextLevelName: Int? = R.string.level_parasol,     // null: já está no nível máximo
    @param:StringRes val nextLevelUnlock: Int? = R.string.level_parasol_unlock,
    val xp: Int = 640,
    val xpGoal: Int = 1000,
    val streakDays: Int = 12,
    val highUvDaysProtected: Int = 4,
    val highUvDays: Int = 5,
    val onTimeReapplications: Int = 38,
    val patches: List<PatchUi> = Patch.entries.map { PatchUi(it.title, it.icon, unlocked = it.ordinal < 5) },
    val editor: ProfileEditorState? = null,     // não null: a folha "Editar perfil" está aberta
) {
    val unlockedPatches: Int get() = patches.count { it.unlocked }
    val totalPatches: Int get() = patches.size
}

// O que se está a escrever na folha "Editar perfil" (só é guardado ao carregar em Guardar)
data class ProfileEditorState(
    val email: String,                  // a conta do Supabase (só para mostrar)
    val name: String,
    val city: String,
    val skinType: SkinType?,
    val isConfirmingDelete: Boolean = false,    // o diálogo "Apagar conta?" está aberto
    val isDeleting: Boolean = false,
    @param:StringRes val error: Int? = null,
)
