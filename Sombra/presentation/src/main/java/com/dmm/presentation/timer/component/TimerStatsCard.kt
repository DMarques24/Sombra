package com.dmm.presentation.timer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Caixa com colunas "etiqueta / valor" separadas por linhas verticais (Aviso às · UV agora · Na água).
@Composable
fun TimerStatsCard(stats: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SombraColors.SurfaceSoft, RoundedCornerShape(24.dp))
            .padding(vertical = 18.dp)
            // IntrinsicSize faz os divisores terem a altura das colunas
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        stats.forEachIndexed { index, (label, value) ->
            if (index > 0) {
                VerticalDivider(modifier = Modifier.fillMaxHeight(), color = SombraColors.Line)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .semantics(mergeDescendants = true) {},
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SombraColors.TextMuted,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = SombraColors.Text,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerStatsCardPreview() {
    SombraTheme {
        TimerStatsCard(
            stats = listOf("Aviso às" to "14:08", "UV agora" to "8", "Na água" to "Não"),
            modifier = Modifier.padding(20.dp),
        )
    }
}
