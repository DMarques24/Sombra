package com.dmm.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

// Barra fina no topo que mostra em que passo de um fluxo estamos (registo, onboarding)
@Composable
fun SombraStepProgressBar(
    step: Int,
    totalSteps: Int,
    description: String,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = { step.toFloat() / totalSteps },
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .semantics { contentDescription = description },     // "Passo 1 de 3"
        color = SombraColors.Text,
        trackColor = SombraColors.Line,
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SombraStepProgressBarPreview() {
    SombraTheme {
        SombraStepProgressBar(step = 1, totalSteps = 3, description = "Passo 1 de 3", modifier = Modifier.padding(20.dp))
    }
}
