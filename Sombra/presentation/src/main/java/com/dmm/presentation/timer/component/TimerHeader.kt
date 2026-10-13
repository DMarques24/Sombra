package com.dmm.presentation.timer.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun TimerHeader(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = SombraColors.Text,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = SombraColors.TextMuted,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerHeaderPreview() {
    SombraTheme {
        TimerHeader(
            title = "Protetor",
            subtitle = "Nenhum temporizador a decorrer.",
            modifier = Modifier.padding(20.dp),
        )
    }
}
