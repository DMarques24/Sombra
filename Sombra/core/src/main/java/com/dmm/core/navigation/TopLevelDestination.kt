package com.dmm.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dmm.core.R

// Os separadores da barra de navegação, pela ordem em que aparecem.
enum class TopLevelDestination(
    val route: Any,
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int,
) {
    HOME(HomeRoute, R.drawable.ic_nav_today, R.string.nav_today),
    FORECAST(ForecastRoute, R.drawable.ic_nav_forecast, R.string.nav_forecast),
    TIMER(TimerRoute, R.drawable.ic_nav_sunscreen, R.string.nav_timer),
    PROFILE(ProfileRoute, R.drawable.ic_nav_profile, R.string.nav_profile),
}
