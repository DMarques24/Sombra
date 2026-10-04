package com.dmm.core.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dmm.core.designsystem.SombraTheme
import com.dmm.core.navigation.TopLevelDestination

// Só desenha a barra: recebe o separador ativo e avisa quando outro é tocado.
// Quem navega de facto é o NavHost, na app.
@Composable
fun SombraNavigationBar(
    currentDestination: TopLevelDestination?,
    onNavigate: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        TopLevelDestination.entries.forEach { destination ->
            val label = stringResource(destination.label)
            NavigationBarItem(
                selected = destination == currentDestination,
                onClick = { onNavigate(destination) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(label) },
            )
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
