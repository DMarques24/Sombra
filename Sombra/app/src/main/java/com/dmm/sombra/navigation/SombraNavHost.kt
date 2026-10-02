package com.dmm.sombra.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dmm.core.navigation.HomeRoute
import com.dmm.presentation.home.homeScreen

@Composable
fun SombraNavHost(modifier: Modifier = Modifier) {
    // Controla a navegação: avançar, voltar atrás
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute,          // primeiro ecrã a aparecer
        modifier = modifier,
    ) {
        homeScreen(
            // o que acontece quando o Início pede para abrir o tipo de pele
            // onOpenSkinType = { navController.navigate(SkinTypeRoute) },
        )
       /* skinTypeScreen(
            onDone = { navController.popBackStack() },   // voltar atrás
            onBack = { navController.popBackStack() },
        )*/
    }
}
