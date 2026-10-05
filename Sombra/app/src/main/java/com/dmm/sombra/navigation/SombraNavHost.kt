package com.dmm.sombra.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dmm.core.components.SombraNavigationBar
import com.dmm.core.navigation.HomeRoute
import com.dmm.core.navigation.LoginRoute
import com.dmm.core.navigation.RegisterRoute
import com.dmm.core.navigation.TopLevelDestination
import com.dmm.presentation.home.homeScreen
import com.dmm.presentation.forecast.forecastScreen
import com.dmm.presentation.timer.timerScreen
import com.dmm.presentation.profile.profileScreen
import com.dmm.presentation.login.loginScreen
import com.dmm.presentation.register.registerScreen

@Composable
fun SombraNavHost(modifier: Modifier = Modifier) {
    // Controla a navegação: avançar, voltar atrás
    val navController = rememberNavController()

    // Descobre qual dos separadores está aberto, para o pintar como selecionado
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = TopLevelDestination.entries.firstOrNull { destination ->
        backStackEntry?.destination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
    }

    // A barra fica aqui (e não em cada ecrã) para ser partilhada por todos os separadores
    Scaffold(
        modifier = modifier,
        bottomBar = {
            // O login e o registo não têm barra de navegação
            val destination = backStackEntry?.destination
            val isAuth = destination?.hasRoute(LoginRoute::class) == true ||
                destination?.hasRoute(RegisterRoute::class) == true
            if (!isAuth) {
                SombraNavigationBar(
                    currentDestination = currentDestination,
                    onNavigate = { navController.navigateToTopLevel(it) },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoginRoute,
            // Só o espaço da barra em baixo: o céu do Início pode ir até ao topo, por trás da status bar
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            homeScreen(
                // o que acontece quando o Início pede para abrir o tipo de pele
                // onOpenSkinType = { navController.navigate(SkinTypeRoute) },
            )
            forecastScreen()
            timerScreen()
            profileScreen()
            loginScreen(
                onLogin = { navController.navigate(RegisterRoute) },
            )
            registerScreen()
            /* skinTypeScreen(
                 onDone = { navController.popBackStack() },   // voltar atrás
                 onBack = { navController.popBackStack() },
             )*/
        }
    }
}

// Troca de separador sem empilhar ecrãs: volta ao Início antes de abrir o novo,
// guarda o estado do separador que sai e repõe o do que entra.
private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

