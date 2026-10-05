package com.dmm.presentation.forecast.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R

// Uma hora do gráfico: a hora do dia (0–23) e o índice UV nessa hora.
data class HourlyUv(
    val hour: Int,
    val uvIndex: Int,
)

// Cor da escala UV da OMS para um valor
fun uvColor(uvIndex: Int): Color = when {
    uvIndex <= 2 -> SombraColors.UvLow
    uvIndex <= 5 -> SombraColors.UvModerate
    uvIndex <= 7 -> SombraColors.UvHigh
    uvIndex <= 10 -> SombraColors.UvVeryHigh
    else -> SombraColors.UvExtreme
}

// Cartão "Índice UV por hora": título, valor atual, gráfico de barras e legenda.
@Composable
fun HourlyUvCard(
    hourly: List<HourlyUv>,
    currentHour: Int,
    currentUv: Int,
    modifier: Modifier = Modifier,
) {
    val items = hourly.map { hour ->
        BarChartItem(
            label = hour.hour.toString(),
            value = hour.uvIndex.toFloat(),
            color = uvColor(hour.uvIndex),
            description = stringResource(R.string.forecast_uv_bar_description, hour.hour, hour.uvIndex),
        )
    }
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .padding(20.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.forecast_uv_title),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.forecast_uv_now, currentUv),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(20.dp))
        BarChart(
            items = items,
            selectedIndex = hourly.indexOfFirst { it.hour == currentHour }.takeIf { it >= 0 },
            // escala fixa (e não o máximo do dia): assim um dia fraco não parece tão alto como um forte
            maxValue = 10f,
        )
        Spacer(Modifier.height(16.dp))
        ChartLegend(
            listOf(
                stringResource(R.string.uv_level_low) to SombraColors.UvLow,
                stringResource(R.string.uv_level_moderate) to SombraColors.UvModerate,
                stringResource(R.string.uv_level_high) to SombraColors.UvHigh,
                stringResource(R.string.uv_level_very_high) to SombraColors.UvVeryHigh,
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F4EE)
@Composable
private fun HourlyUvCardPreview() {
    SombraTheme {
        HourlyUvCard(
            hourly = listOf(1, 2, 4, 6, 8, 9, 9, 7, 5, 3, 2, 1)
                .mapIndexed { i, uv -> HourlyUv(hour = 8 + i, uvIndex = uv) },
            currentHour = 13,
            currentUv = 8,
            modifier = Modifier.padding(20.dp),
        )
    }
}
