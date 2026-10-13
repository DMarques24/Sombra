package com.dmm.presentation.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.HomeRoute


fun NavGraphBuilder.homeScreen(onOpenTimer: () -> Unit) {
    composable<HomeRoute> {
        HomeScreen(onOpenTimer = onOpenTimer)
    }
}
