package com.dmm.presentation.timer

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.TimerRoute

fun NavGraphBuilder.timerScreen() {
    composable<TimerRoute> {
        TimerScreen()
    }
}
