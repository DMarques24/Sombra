package com.dmm.core.designsystem

import androidx.compose.ui.graphics.Color

object SombraColors {
    // ---------- Marca ----------
    val Ink = Color(0xFF2B1600)            // castanho escuro: fundo do ícone, splash
    val Sun = Color(0xFFFFC23A)            // amarelo do sol
    val SunStrong = Color(0xFFFFB21A)
    val SunDeep = Color(0xFFF07F14)        // laranja: metade do sol em sombra
    val SunContainer = Color(0xFFFFE7B0)
    val SunContainerDark = Color(0xFF4A2A05)

    // ---------- Neutros (tema claro) ----------
    val Text = Color(0xFF141310)
    val TextMuted = Color(0xFF6F6A60)
    val Surface = Color.White
    val SurfaceSoft = Color(0xFFF4F2EC)
    val Line = Color(0xFFE7E3DA)

    // ---------- Neutros (tema escuro) ----------
    val TextDark = Color(0xFFF1EEE6)
    val TextMutedDark = Color(0xFFA19B8F)
    val SurfaceDark = Color(0xFF151413)
    val SurfaceSoftDark = Color(0xFF221F1C)
    val LineDark = Color(0xFF2C2B28)

    // ---------- Céu ----------
    val SkyBlue = Color(0xFF2F8EF0)
    val SkyBlueLight = Color(0xFF6DB4FA)
    val SkyBluePale = Color(0xFFBFE0FB)
    val SkyBlueDeep = Color(0xFF0B2E52)
    val SkyContainerDark = Color(0xFF123A63)
    val OnSky = Color(0xFFFFF8EC)          // texto creme por cima do céu

    // ---------- Escala UV (OMS) ----------
    val UvLow = Color(0xFF3AA655)          // 0–2
    val UvModerate = Color(0xFFF2C61F)     // 3–5
    val UvHigh = Color(0xFFF28A1B)         // 6–7
    val UvVeryHigh = Color(0xFFE0432B)     // 8–10
    val UvExtreme = Color(0xFF8D49C9)      // 11+
    val OnUvModerate = Color(0xFF3D2D00)   // texto sobre o amarelo (os outros levam branco)

    // ---------- Cartões de aviso ----------
    val RiskHighContainer = Color(0xFFFDE7DF)
    val OnRiskHighContainer = Color(0xFF7A2210)
    val RiskHighContainerDark = Color(0xFF4A1509)
    val RiskModerateContainer = Color(0xFFFDF4D2)
    val OnRiskModerateContainer = Color(0xFF6A4C00)
    val RiskModerateIcon = Color(0xFFD9A800)
    val Success = UvLow                    // "Forte", janelas seguras

    // ---------- Gradientes ----------
    // Brush.verticalGradient(SombraColors.SkySunny)
    val SkySunny = listOf(SkyBlue, SkyBlueLight, SkyBluePale)
    val SkyCloudy = listOf(Color(0xFF6F8499), Color(0xFF98ABBD), Color(0xFFCDD7E0))
    val SkyLogin = listOf(Color(0xFF8FC7F7), Color(0xFFCFE7FB), Color.White)
    val HomeGround = Color(0xFFF6F4EE)     // fundo por baixo do céu no ecrã Início

    // Brush.radialGradient(SombraColors.Splash), do centro para fora
    val Splash = listOf(Color(0xFFFFE08A), Color(0xFFFFB640), Color(0xFFF5812A), Color(0xFFE4632A))

    // Brush.horizontalGradient(SombraColors.SunButton), com texto na cor Ink
    val SunButton = listOf(Color(0xFFFF8A1F), Sun)

    // ---------- Tipos de pele (Fitzpatrick I a VI) ----------
    val Skin = listOf(
        Color(0xFFF7E1D0), Color(0xFFEECBB0), Color(0xFFD9A882),
        Color(0xFFB77C52), Color(0xFF8A5634), Color(0xFF4F3020),
    )

    // ---------- Patches ----------
    val PatchPrimeiroProtetor = Color(0xFFF08A24)
    val PatchSemanaSpf = UvVeryHigh
    val PatchReaplicador = SkyBlue
    val PatchHoraDaSombra = UvLow
    val PatchChapeuNoTopo = UvExtreme
    val PatchUvExtremo = Color(0xFF6B3FA0)
    val PatchVeraoLimpo = Color(0xFF0FA3A3)
    val PatchFamilia = Color(0xFFD9568C)
    val PatchLocked = SurfaceSoft
    val PatchLockedIcon = Color(0xFFB9B3A6)
    val PatchLockedStitch = Color(0xFFD3CEC2)

    // ---------- Níveis (1 a 5) ----------
    val Levels = listOf(
        Color(0xFF9DB4C8), // 1 Sombrinha
        UvLow,                 // 2 Filtro
        PatchPrimeiroProtetor, // 3 Chapéu
        UvVeryHigh,            // 4 Guarda-sol
        Ink,                   // 5 Eclipse
    )
}

