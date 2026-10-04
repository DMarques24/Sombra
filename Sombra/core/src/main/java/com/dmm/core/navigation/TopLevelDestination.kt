package com.dmm.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.dmm.core.R

// Os separadores da barra de navegação, pela ordem em que aparecem.
enum class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    @param:StringRes val label: Int,
) {
    HOME(HomeRoute, Icons.Rounded.Home, R.string.nav_home),
    FORECAST(ForecastRoute, Icons.Rounded.CalendarMonth, R.string.nav_forecast),
    TIMER(TimerRoute, Icons.Rounded.Timer, R.string.nav_timer),
    PROFILE(ProfileRoute, Icons.Rounded.Person, R.string.nav_profile),
}
