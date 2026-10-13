package com.dmm.presentation.register.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.register.PasswordStrength

@Composable
fun PasswordStrengthLabel(strength: PasswordStrength, modifier: Modifier = Modifier) {
    val (text, color) = when (strength) {
        PasswordStrength.WEAK -> R.string.password_weak to SombraColors.UvVeryHigh
        PasswordStrength.MEDIUM -> R.string.password_medium to SombraColors.RiskModerateIcon
        PasswordStrength.STRONG -> R.string.password_strong to SombraColors.Success
    }
    Text(
        text = stringResource(text),
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = color,
    )
}

@Preview(showBackground = true)
@Composable
private fun PasswordStrengthLabelPreview() {
    SombraTheme {
        Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PasswordStrength.entries.forEach { PasswordStrengthLabel(it) }
        }
    }
}
