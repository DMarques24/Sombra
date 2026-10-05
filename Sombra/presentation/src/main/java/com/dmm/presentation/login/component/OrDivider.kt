package com.dmm.presentation.login.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun OrDivider(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = SombraColors.Line)
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = SombraColors.TextMuted,
        )
        HorizontalDivider(Modifier.weight(1f), color = SombraColors.Line)
    }
}

@Preview(showBackground = true)
@Composable
private fun OrDividerPreview() {
    SombraTheme { OrDivider(text = "ou", modifier = Modifier.padding(20.dp)) }
}
