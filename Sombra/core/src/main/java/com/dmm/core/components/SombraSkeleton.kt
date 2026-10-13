package com.dmm.core.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme


@Composable
fun SombraSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    color: Color = SombraColors.SurfaceSoft,
    highlight: Color = Color.White.copy(alpha = 0.7f),
) {
    val transition = rememberInfiniteTransition(label = "skeleton")

    val shine = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1_200, easing = LinearEasing)),
        label = "skeleton-shine",
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .drawBehind {
                val band = size.width
                val start = -band + (size.width + band) * shine.value
                drawRect(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, highlight, Color.Transparent),
                        startX = start,
                        endX = start + band,
                    ),
                )
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun SombraSkeletonPreview() {
    SombraTheme {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SombraSkeleton(Modifier.size(56.dp), shape = CircleShape)
            SombraSkeleton(Modifier.size(width = 160.dp, height = 18.dp), shape = CircleShape)
            SombraSkeleton(Modifier.size(width = 120.dp, height = 14.dp), shape = CircleShape)
        }
    }
}
