package com.dmm.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Botão secundário com contorno. O ícone é opcional e aparece à esquerda do texto
// com as cores originais (ex.: logótipos Apple e Google no login).
@Composable
fun SombraSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(64.dp),
        shape = CircleShape,
        border = BorderStroke(2.dp, SombraColors.Line),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SombraColors.Text),
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,          // o texto já diz o que é
                tint = Color.Unspecified,           // não pinta por cima das cores do logótipo
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(12.dp))
        }
        SombraButtonLabel(text)
    }
}

@Preview(showBackground = true)
@Composable
private fun SombraSecondaryButtonPreview() {
    SombraTheme {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SombraSecondaryButton(text = "Já saí do sol", onClick = {}, modifier = Modifier.weight(1f))
            SombraSecondaryButton(
                text = "Com ícone",
                onClick = {},
                icon = rememberVectorPainter(Icons.Rounded.Star),
                modifier = Modifier.weight(1f),
            )
        }
    }
}
