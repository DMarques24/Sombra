package com.dmm.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.designsystem.SombraTheme

// Liga o ViewModel ao ecrã. É isto que a navegação chama.
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState)
}

// Só desenha o estado que recebe: fácil de pré-visualizar e testar.
@Composable
fun HomeContent(uiState: HomeUiState, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    SombraTheme {
        HomeContent(uiState = HomeUiState())
    }
}
