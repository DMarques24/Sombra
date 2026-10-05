package com.dmm.presentation.timer.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Laranja -> amarelo à volta do anel, como no mock "a decorrer"
val TimerRingSunBrush = Brush.sweepGradient(listOf(SombraColors.SunDeep, SombraColors.Sun, SombraColors.SunDeep))

// Anel com o tempo no meio. progress é a parte pintada (0f = só o fundo, 1f = anel completo),
// a começar no topo e a andar no sentido dos ponteiros do relógio.
@Composable
fun TimerRing(
    time: String,
    label: String,
    progress: Float,
    modifier: Modifier = Modifier,
    progressBrush: Brush = TimerRingSunBrush,
    trackColor: Color = SombraColors.SurfaceSoft,
    timeColor: Color = SombraColors.Text,
    size: Dp = 210.dp,
    strokeWidth: Dp = 18.dp,
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke),
            )
            // O gradiente "sweep" começa às 3h; rodamos -90° para o arco e o gradiente começarem no topo
            rotate(degrees = -90f) {
                drawArc(
                    brush = progressBrush,
                    startAngle = 0f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = time,
                style = TextStyle(
                    fontSize = 48.sp,
                    lineHeight = 52.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                ),
                color = timeColor,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = SombraColors.TextMuted,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerRingPreview() {
    SombraTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TimerRing(time = "2:00", label = "horas até reaplicar", progress = 0f, timeColor = SombraColors.TextMuted, size = 160.dp)
            TimerRing(time = "1:12:40", label = "até reaplicar", progress = 0.6f, size = 160.dp)
            TimerRing(
                time = "0:00",
                label = "terminou há 6 min",
                progress = 1f,
                progressBrush = SolidColor(SombraColors.UvVeryHigh),
                timeColor = SombraColors.UvVeryHigh,
                size = 160.dp,
            )
        }
    }
}
