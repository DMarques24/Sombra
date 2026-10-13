package com.dmm.core.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraTheme
import com.dmm.core.navigation.TopLevelDestination

@Composable
fun SombraNavigationBar(
    currentDestination: TopLevelDestination?,
    onNavigate: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    // A Surface dá a forma de "pílula", a sombra e o espaço à volta.
    Surface(
        modifier = modifier
            .navigationBarsPadding()                          // não fica por baixo dos gestos do sistema
            .padding(horizontal = 16.dp, vertical = 12.dp),   // afasta a barra das margens do ecrã
        shape = CircleShape,                                  // bordas totalmente redondas
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        NavigationBar(
            modifier = Modifier.height(68.dp),
            containerColor = Color.Transparent,   // quem pinta o fundo é a Surface
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0),       // o espaço do sistema já foi tratado na Surface
        ) {
            TopLevelDestination.entries.forEach { destination ->
                val label = stringResource(destination.label)
                NavigationBarItem(
                    selected = destination == currentDestination,
                    onClick = { onNavigate(destination) },
                    icon = { Icon(painterResource(destination.icon), contentDescription = null) },
                    label = { Text(label) },
                    colors = NavigationBarItemDefaults.colors(
                        // Separador ativo
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant,   // fundo bege atrás do ícone
                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        // Separadores inativos
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SombraNavigationBarPreview() {
    SombraTheme {
        SombraNavigationBar(
            currentDestination = TopLevelDestination.HOME,
            onNavigate = {},
        )
    }
}
