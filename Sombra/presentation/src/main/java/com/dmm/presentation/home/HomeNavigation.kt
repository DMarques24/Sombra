package com.dmm.presentation.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmm.core.navigation.HomeRoute

// Regista o ecrã Início no NavHost. Os eventos de navegação chegam como parâmetros
// (ex.: onOpenSkinType), assim o ecrã não precisa de conhecer o NavController.
fun NavGraphBuilder.homeScreen() {
    composable<HomeRoute> {
        HomeScreen()
    }
}
