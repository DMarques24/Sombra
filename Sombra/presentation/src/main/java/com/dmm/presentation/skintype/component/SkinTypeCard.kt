package com.dmm.presentation.skintype.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
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

// Cartão de um tipo de pele: amostra da cor, "Tipo II" e a frase.
// Escolhido fica bege com contorno escuro; os outros brancos com contorno claro.
@Composable
fun SkinTypeCard(
    title: String,
    description: String,
    swatch: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier.selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        shape = shape,
        color = if (selected) SombraColors.SurfaceSoft else Color.White,
        border = BorderStroke(2.dp, if (selected) SombraColors.Text else SombraColors.Line),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .background(swatch, CircleShape)
                    // contorno quase invisível para os tons claros não desaparecerem no branco
                    .border(1.dp, Color.Black.copy(alpha = 0.06f), CircleShape)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SombraColors.Text,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SombraColors.Text,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SkinTypeCardPreview() {
    SombraTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SkinTypeCard("Tipo I", "Queima sempre", SombraColors.Skin[0], selected = false, onClick = {}, modifier = Modifier.weight(1f))
            SkinTypeCard("Tipo II", "Queima fácil", SombraColors.Skin[1], selected = true, onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}
