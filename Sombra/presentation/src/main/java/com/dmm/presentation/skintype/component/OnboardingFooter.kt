package com.dmm.presentation.skintype.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Rodapé: "Saltar" à esquerda; à direita o resumo ("2 escolhidos") e o botão redondo para avançar
@Composable
fun OnboardingFooter(
    skipText: String,
    summary: String,
    nextDescription: String,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onSkip) {
            Text(
                text = skipText,
                style = MaterialTheme.typography.bodyLarge,
                color = SombraColors.TextMuted,
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = summary,
            style = MaterialTheme.typography.bodyLarge,
            color = SombraColors.Text,
        )
        Spacer(Modifier.width(16.dp))
        FilledIconButton(
            onClick = onNext,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = SombraColors.Text,
                contentColor = Color.White,
            ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = nextDescription,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingFooterPreview() {
    SombraTheme {
        OnboardingFooter(
            skipText = "Saltar",
            summary = "2 escolhidos",
            nextDescription = "Seguinte",
            onSkip = {},
            onNext = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
