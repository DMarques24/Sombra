package com.dmm.presentation.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.LoginRoute

fun NavGraphBuilder.loginScreen(
    onSignedIn: (onboardingCompleted: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
) {
    composable<LoginRoute> {
        LoginScreen(onSignedIn = onSignedIn, onCreateAccount = onCreateAccount)
    }
}
