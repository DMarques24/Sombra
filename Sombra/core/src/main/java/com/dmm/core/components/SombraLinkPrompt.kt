package com.dmm.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Pergunta + ação a negrito (ex.: "Ainda não tens conta? Criar conta"). Só a parte a negrito é clicável.
@Composable
fun SombraLinkPrompt(
    question: String,
    action: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            color = SombraColors.TextMuted,
        )
        TextButton(onClick = onActionClick) {
            Text(
                text = action,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = SombraColors.Text,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SombraLinkPromptPreview() {
    SombraTheme {
        SombraLinkPrompt(
            question = "Ainda não tens conta?",
            action = "Criar conta",
            onActionClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
