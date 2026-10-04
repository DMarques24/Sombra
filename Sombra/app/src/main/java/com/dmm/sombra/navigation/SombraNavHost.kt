package com.dmm.sombra.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dmm.core.components.SombraNavigationBar
import com.dmm.core.components.SombraTopBar
import com.dmm.core.navigation.ForecastRoute
import com.dmm.core.navigation.HomeRoute
import com.dmm.core.navigation.ProfileRoute
import com.dmm.core.navigation.TimerRoute
import com.dmm.core.navigation.TopLevelDestination
import com.dmm.presentation.home.homeScreen

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
            SombraNavigationBar(
                currentDestination = currentDestination,
                onNavigate = { navController.navigateToTopLevel(it) },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,          // primeiro ecrã a aparecer
            // Só o espaço da barra em baixo: o céu do Início pode ir até ao topo, por trás da status bar
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            homeScreen(
                // o que acontece quando o Início pede para abrir o tipo de pele
                // onOpenSkinType = { navController.navigate(SkinTypeRoute) },
            )
            // TODO: trocar pelos ecrãs verdadeiros quando existirem
            composable<ForecastRoute> { PlaceholderScreen(TopLevelDestination.FORECAST) }
            composable<TimerRoute> { PlaceholderScreen(TopLevelDestination.TIMER) }
            composable<ProfileRoute> { PlaceholderScreen(TopLevelDestination.PROFILE) }
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

@Composable
private fun PlaceholderScreen(destination: TopLevelDestination) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(destination.label),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
