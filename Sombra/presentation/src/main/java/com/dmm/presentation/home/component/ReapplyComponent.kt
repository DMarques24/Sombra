package com.dmm.presentation.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R

@Composable
fun ReapplyComponent(
    title: String,
    description: String,
    progress: Float,
    onReapply: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = stringResource(R.string.reapply_button),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(64.dp),
            color = SombraColors.SunDeep,
            trackColor = SombraColors.Line,
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Butt,
            gapSize = 0.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SombraColors.Text,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = SombraColors.TextMuted,
            )
        }
        OutlinedButton(
            onClick = onReapply,
            border = BorderStroke(2.dp, SombraColors.Text),
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.titleSmall,
                color = SombraColors.Text,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReapplyComponentPreview() {
    SombraTheme {
        ReapplyComponent(
            title = "Reaplicar em 1h 12m",
            description = "SPF 50 aplicado às 12:08",
            progress = 0.4f,
            onReapply = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
