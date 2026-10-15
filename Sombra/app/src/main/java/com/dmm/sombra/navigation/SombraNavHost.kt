package com.dmm.sombra.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dmm.core.components.SombraNavigationBar
import com.dmm.core.navigation.HomeRoute
import com.dmm.core.navigation.LoginRoute
import com.dmm.core.navigation.RegisterRoute
import com.dmm.core.navigation.SkinTypeRoute
import com.dmm.core.navigation.TopLevelDestination
import com.dmm.presentation.home.homeScreen
import com.dmm.presentation.forecast.forecastScreen
import com.dmm.presentation.timer.timerScreen
import com.dmm.presentation.profile.profileScreen
import com.dmm.presentation.login.loginScreen
import com.dmm.presentation.register.registerScreen
import com.dmm.presentation.skintype.skinTypeScreen

@Composable
fun SombraNavHost(startDestination: Any, modifier: Modifier = Modifier) {
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
            // Só os 4 separadores têm barra; login, registo e tipo de pele não
            if (currentDestination != null) {
                SombraNavigationBar(
                    currentDestination = currentDestination,
                    onNavigate = { navController.navigateToTopLevel(it) },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            // Só o espaço da barra em baixo: o céu do Início pode ir até ao topo, por trás da status bar
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            homeScreen(
                onOpenTimer = { navController.navigateToTopLevel(TopLevelDestination.TIMER) },
            )
            forecastScreen()
            timerScreen()
            profileScreen(
                onSignedOut = { navController.navigateClearingStack(LoginRoute) },
            )
            // Entrou no Supabase: se já escolheu o tipo de pele vai para o Início, senão acaba o registo
            loginScreen(
                onSignedIn = { onboardingCompleted ->
                    if (onboardingCompleted) navController.navigateClearingStack(HomeRoute)
                    else navController.navigate(SkinTypeRoute)
                },
                onCreateAccount = { navController.navigate(RegisterRoute) },
            )
            registerScreen(
                onContinue = { navController.navigate(SkinTypeRoute) },
                onLogin = {
                    if (!navController.popBackStack<LoginRoute>(inclusive = false)) navController.navigate(LoginRoute)
                },
            )
            skinTypeScreen(
                // Registo acabado: o Início fica como único ecrã na pilha (o "voltar" já não regressa ao registo)
                onNext = { navController.navigateClearingStack(HomeRoute) },
            )
        }
    }
}

// Abre o ecrã como o único da pilha: o "voltar" já não regressa ao Login/registo (nem ao Perfil depois de sair)
private fun NavHostController.navigateClearingStack(route: Any) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
    }
}

// Troca de separador sem empilhar ecrãs: volta ao Início antes de abrir o novo,
// guarda o estado do separador que sai e repõe o do que entra.
// É o Início (e não o início do grafo) porque, depois do registo, o Login já não está na pilha.
private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo<HomeRoute> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

