package com.dmm.presentation.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.dmm.core.designsystem.SombraColors
import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.profile.model.SunPlace
import com.dmm.domain.progress.model.Level
import com.dmm.domain.progress.model.Patch
import com.dmm.presentation.R

// Como os enums do domínio aparecem no ecrã: textos, cores e imagens

// Os nomes do enum já são a numeração romana: I, II, III...
val SkinType.roman: String get() = name

@get:StringRes
val SkinType.description: Int
    get() = when (this) {
        SkinType.I -> R.string.skin_type_1
        SkinType.II -> R.string.skin_type_2
        SkinType.III -> R.string.skin_type_3
        SkinType.IV -> R.string.skin_type_4
        SkinType.V -> R.string.skin_type_5
        SkinType.VI -> R.string.skin_type_6
    }

val SkinType.color: Color get() = SombraColors.Skin[ordinal]

@get:StringRes
val SunPlace.label: Int
    get() = when (this) {
        SunPlace.Beach -> R.string.sun_place_beach
        SunPlace.OutdoorWork -> R.string.sun_place_outdoor_work
        SunPlace.Running -> R.string.sun_place_running
        SunPlace.City -> R.string.sun_place_city
        SunPlace.Mountain -> R.string.sun_place_mountain
    }

@get:StringRes
val Level.title: Int
    get() = when (this) {
        Level.Sunglasses -> R.string.level_sunglasses
        Level.Cap -> R.string.level_cap
        Level.Hat -> R.string.level_hat
        Level.Parasol -> R.string.level_parasol
        Level.Shade -> R.string.level_shade
    }

// O que se ganha ao chegar a este nível
@get:StringRes
val Level.unlock: Int
    get() = when (this) {
        Level.Sunglasses -> R.string.level_sunglasses_unlock
        Level.Cap -> R.string.level_cap_unlock
        Level.Hat -> R.string.level_hat_unlock
        Level.Parasol -> R.string.level_parasol_unlock
        Level.Shade -> R.string.level_shade_unlock
    }

@get:StringRes
val Patch.title: Int
    get() = when (this) {
        Patch.FirstSunscreen -> R.string.patch_first_sunscreen
        Patch.SpfWeek -> R.string.patch_spf_week
        Patch.Reapplier -> R.string.patch_reapplier
        Patch.ShadeHour -> R.string.patch_shade_hour
        Patch.HatOnTop -> R.string.patch_hat_on_top
        Patch.ExtremeUv -> R.string.patch_extreme_uv
        Patch.ProtectedSummer -> R.string.patch_protected_summer
        Patch.Family -> R.string.patch_family
    }

@get:DrawableRes
val Patch.icon: Int
    get() = when (this) {
        Patch.FirstSunscreen -> R.drawable.patch_primeiro_protetor
        Patch.SpfWeek -> R.drawable.patch_semana_spf
        Patch.Reapplier -> R.drawable.patch_reaplicador
        Patch.ShadeHour -> R.drawable.patch_hora_da_sombra
        Patch.HatOnTop -> R.drawable.patch_chapeu_no_topo
        Patch.ExtremeUv -> R.drawable.patch_uv_extremo
        Patch.ProtectedSummer -> R.drawable.patch_verao_limpo
        Patch.Family -> R.drawable.patch_familia
    }
