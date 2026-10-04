package com.dmm.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SombraColors.Text,                 // botões pretos
    onPrimary = SombraColors.Surface,
    primaryContainer = SombraColors.Ink,
    onPrimaryContainer = SombraColors.Sun,

    secondary = SombraColors.SunDeep,
    onSecondary = SombraColors.Ink,
    secondaryContainer = SombraColors.SunContainer,
    onSecondaryContainer = SombraColors.Ink,

    tertiary = SombraColors.SkyBlue,
    onTertiary = Color.White,
    tertiaryContainer = SombraColors.SkyBluePale,
    onTertiaryContainer = SombraColors.SkyBlueDeep,

    background = SombraColors.Surface,
    onBackground = SombraColors.Text,
    surface = SombraColors.Surface,
    onSurface = SombraColors.Text,
    surfaceVariant = SombraColors.SurfaceSoft,   // campos de texto, cartões suaves
    onSurfaceVariant = SombraColors.TextMuted,   // texto secundário
    surfaceContainerLowest = SombraColors.Surface,
    surfaceContainerLow = SombraColors.Surface,
    surfaceContainer = SombraColors.Surface,
    surfaceContainerHigh = SombraColors.SurfaceSoft,
    surfaceContainerHighest = SombraColors.SurfaceSoft,

    outline = SombraColors.Line,
    outlineVariant = SombraColors.SurfaceSoft,

    error = SombraColors.UvVeryHigh,
    onError = Color.White,
    errorContainer = SombraColors.RiskHighContainer,
    onErrorContainer = SombraColors.OnRiskHighContainer,
)

private val DarkColorScheme = darkColorScheme(
    primary = SombraColors.TextDark,
    onPrimary = SombraColors.SurfaceDark,
    primaryContainer = SombraColors.Ink,
    onPrimaryContainer = SombraColors.Sun,

    secondary = SombraColors.SunStrong,
    onSecondary = SombraColors.Ink,
    secondaryContainer = SombraColors.SunContainerDark,
    onSecondaryContainer = SombraColors.SunContainer,

    tertiary = SombraColors.SkyBlueLight,
    onTertiary = SombraColors.SkyBlueDeep,
    tertiaryContainer = SombraColors.SkyContainerDark,
    onTertiaryContainer = SombraColors.SkyBluePale,

    background = SombraColors.SurfaceDark,
    onBackground = SombraColors.TextDark,
    surface = SombraColors.SurfaceDark,
    onSurface = SombraColors.TextDark,
    surfaceVariant = SombraColors.SurfaceSoftDark,
    onSurfaceVariant = SombraColors.TextMutedDark,
    surfaceContainerLowest = SombraColors.SurfaceDark,
    surfaceContainerLow = SombraColors.SurfaceDark,
    surfaceContainer = SombraColors.SurfaceDark,
    surfaceContainerHigh = SombraColors.SurfaceSoftDark,
    surfaceContainerHighest = SombraColors.SurfaceSoftDark,

    outline = SombraColors.LineDark,
    outlineVariant = SombraColors.SurfaceSoftDark,

    error = SombraColors.UvVeryHigh,
    onError = Color.White,
    errorContainer = SombraColors.RiskHighContainerDark,
    onErrorContainer = SombraColors.RiskHighContainer,
)

@Composable
fun SombraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
