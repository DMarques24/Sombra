package com.dmm.presentation.forecast.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

data class BarChartItem(
    val label: String,
    val value: Float,
    val color: Color,
    val description: String = label,
)

@Composable
fun BarChart(
    items: List<BarChartItem>,
    modifier: Modifier = Modifier,
    selectedIndex: Int? = null,
    maxValue: Float = items.maxOfOrNull { it.value } ?: 1f,
    barAreaHeight: Dp = 110.dp,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {

        // As barras
        Row(
            modifier = Modifier.fillMaxWidth().height(barAreaHeight),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            items.forEachIndexed { index, item ->
                val fraction = (item.value / maxValue).coerceIn(0.06f, 1f)
                val shape = RoundedCornerShape(8.dp)
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .semantics { contentDescription = item.description }
                        .then(
                            if (isSelected) {
                                Modifier
                                    .border(2.dp, MaterialTheme.colorScheme.onSurface, shape)
                                    .padding(3.dp)
                            } else {
                                Modifier
                            }
                        ),
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(item.color, RoundedCornerShape(if (isSelected) 5.dp else 8.dp))
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items.forEach { item ->
                Text(
                    text = item.label,
                    modifier = Modifier.weight(1f).clearAndSetSemantics { },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BarChartPreview() {
    SombraTheme {
        BarChart(
            items = listOf(
                BarChartItem("A", 2f, SombraColors.UvLow),
                BarChartItem("B", 5f, SombraColors.UvModerate),
                BarChartItem("C", 9f, SombraColors.UvVeryHigh),
                BarChartItem("D", 6f, SombraColors.UvHigh),
            ),
            selectedIndex = 2,
        )
    }
}
