package com.dmm.presentation.forecast.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Uma linha da previsão por dia: nome do dia, ícone do tempo, barra do UV máximo e o número numa pílula.
@Composable
fun UvDayCard(
    day: String,
    uvLevel: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    maxUv: Int = 10,
) {
    val color = uvColor(uvLevel)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            // um leitor de ecrã lê a linha inteira de uma vez: "Amanhã, UV máximo 8"
            .semantics(mergeDescendants = true) { contentDescription = "$day, UV máximo $uvLevel" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = day,
            modifier = Modifier.width(96.dp).clearAndSetSemantics { },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp),
        )
        LinearProgressIndicator(
            progress = { (uvLevel.toFloat() / maxUv).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
                .height(10.dp),
            color = color,
            trackColor = SombraColors.Line,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(40.dp)
                .background(uvBadgeColor(uvLevel), CircleShape)
                .clearAndSetSemantics { },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = uvLevel.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

// Na pílula, o amarelo do moderado é escurecido para o número branco continuar legível
private fun uvBadgeColor(uvLevel: Int): Color =
    if (uvLevel in 3..5) SombraColors.RiskModerateIcon else uvColor(uvLevel)

@Preview(showBackground = true)
@Composable
private fun UvDayCardPreview() {
    SombraTheme {
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.Center) {
            UvDayCard(day = "Amanhã", uvLevel = 8, icon = Icons.Rounded.WbSunny)
            HorizontalDivider(color = SombraColors.Line)
            UvDayCard(day = "Sáb", uvLevel = 5, icon = Icons.Rounded.Cloud)
            HorizontalDivider(color = SombraColors.Line)
            UvDayCard(day = "Dom", uvLevel = 7, icon = Icons.Rounded.WbSunny)
        }
    }
}
