package com.dmm.presentation.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.profile.PatchUi
import com.dmm.presentation.profile.ProfileUiState

private const val PatchesPerRow = 4

// Título "Patches ...... 5 de 12" e a grelha de 4 colunas.
// Não usa LazyVerticalGrid porque o ecrã já faz scroll (grelhas "lazy" dentro de scroll rebentam).
@Composable
fun PatchesSection(
    title: String,
    count: String,
    patches: List<PatchUi>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SombraColors.Text,
            )
            Text(
                text = count,
                style = MaterialTheme.typography.bodyLarge,
                color = SombraColors.TextMuted,
            )
        }
        patches.chunked(PatchesPerRow).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { patch ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                        PatchBadge(
                            name = stringResource(patch.name),
                            icon = patch.icon,
                            unlocked = patch.unlocked,
                        )
                    }
                }
                // completa a última linha para os patches não esticarem
                repeat(PatchesPerRow - row.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PatchesSectionPreview() {
    SombraTheme {
        val state = ProfileUiState()
        PatchesSection(
            title = "Patches",
            count = "${state.unlockedPatches} de ${state.totalPatches}",
            patches = state.patches,
            modifier = Modifier.padding(20.dp),
        )
    }
}
