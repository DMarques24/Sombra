package com.dmm.presentation.timer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Cartão com título, explicação e um interruptor. O cartão inteiro é clicável, não só o Switch.
@Composable
fun WaterToggleCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SombraColors.SurfaceSoft, RoundedCornerShape(20.dp))
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Switch)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SombraColors.Text,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SombraColors.TextMuted,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = null,          // o clique é tratado pelo cartão (toggleable)
            colors = SwitchDefaults.colors(
                checkedTrackColor = SombraColors.SkyBlue,
                uncheckedTrackColor = SombraColors.Line,
                uncheckedThumbColor = Color.White,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WaterToggleCardPreview() {
    SombraTheme {
        WaterToggleCard(
            title = "Vou para a água",
            subtitle = "Avisa ao fim de 40 min",
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
