package com.dmm.presentation.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun LevelProgress(
    level: String,
    xp: String,
    progress: Float,
    next: String,
    modifier: Modifier = Modifier,
) {
    val fraction = progress.coerceIn(0f, 1f)
    Column(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = level,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SombraColors.Text,
            )
            Text(
                text = xp,
                style = MaterialTheme.typography.bodyLarge,
                color = SombraColors.TextMuted,
            )
        }
        Spacer(Modifier.height(10.dp))
        // O LinearProgressIndicator não aceita gradientes, por isso a barra é desenhada à mão
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(SombraColors.SurfaceSoft, CircleShape)
                .semantics { progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f) },
        ) {
            Box(
                Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(Brush.horizontalGradient(SombraColors.SunButton), CircleShape)
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = next,
            style = MaterialTheme.typography.bodyMedium,
            color = SombraColors.TextMuted,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LevelProgressPreview() {
    SombraTheme {
        LevelProgress(
            level = "Nível 3 · Chapéu",
            xp = "640 / 1000 XP",
            progress = 0.64f,
            next = "Próximo: Guarda-sol · desbloqueia modo família",
            modifier = Modifier.padding(20.dp),
        )
    }
}
