package com.dmm.presentation.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.RegisterRoute

fun NavGraphBuilder.registerScreen(onContinue: () -> Unit) {
    composable<RegisterRoute> {
        RegisterScreen(onContinue = onContinue)
    }
}
