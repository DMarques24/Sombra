package com.dmm.presentation.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.LoginRoute

fun NavGraphBuilder.loginScreen(onLogin: () -> Unit) {
    composable<LoginRoute> {
        LoginScreen(onLogin = onLogin)
    }
}
