package com.dmm.presentation.skintype.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.skintype.SunPlace

// "ONDE PASSAS MAIS TEMPO AO SOL?" e as pílulas; podem escolher-se várias.
// FlowRow passa para a linha seguinte quando não cabe.
@Composable
fun SunPlaceChips(
    title: String,
    selected: Set<SunPlace>,
    onToggle: (SunPlace) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = SombraColors.TextMuted,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SunPlace.entries.forEach { place ->
                val isSelected = place in selected
                Surface(
                    modifier = Modifier.toggleable(
                        value = isSelected,
                        onValueChange = { onToggle(place) },
                        role = Role.Checkbox,
                    ),
                    shape = CircleShape,
                    color = if (isSelected) SombraColors.Text else Color.White,
                    border = if (isSelected) null else BorderStroke(2.dp, SombraColors.Text),
                ) {
                    Text(
                        text = stringResource(place.label),
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else SombraColors.Text,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SunPlaceChipsPreview() {
    SombraTheme {
        SunPlaceChips(
            title = "ONDE PASSAS MAIS TEMPO AO SOL?",
            selected = setOf(SunPlace.BEACH, SunPlace.RUNNING),
            onToggle = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
