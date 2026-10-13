package com.dmm.presentation.timer.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.Box

@Composable
fun SpfSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SombraColors.TextMuted,
        )
        Row(
            modifier = Modifier.fillMaxWidth().selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .selectable(
                            selected = isSelected,
                            onClick = { onSelect(option) },
                            role = Role.RadioButton,
                        ),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) SombraColors.Text else Color.White,
                    border = if (isSelected) null else BorderStroke(2.dp, SombraColors.Line),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else SombraColors.Text,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpfSelectorPreview() {
    SombraTheme {
        SpfSelector(
            label = "Fator que vais usar",
            options = listOf("15", "30", "50", "50+"),
            selected = "50",
            onSelect = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
