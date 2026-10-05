package com.dmm.presentation.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SombraColors.SurfaceSoft, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 16.dp)
            .semantics(mergeDescendants = true) {},
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = SombraColors.Text,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SombraColors.TextMuted,
        )
    }
}

// Os três cartões lado a lado, todos com a altura do mais alto
@Composable
fun StatCardsRow(stats: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        stats.forEach { (value, label) ->
            StatCard(value = value, label = label, modifier = Modifier.weight(1f).fillMaxHeight())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatCardsRowPreview() {
    SombraTheme {
        StatCardsRow(
            stats = listOf(
                "12" to "dias seguidos",
                "4/5" to "dias de UV alto protegidos",
                "38" to "reaplicações a tempo",
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}
