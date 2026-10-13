package com.dmm.presentation.profile.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R

// Um patch: o crachá redondo e o nome por baixo. Bloqueado mostra o cadeado e o nome a cinzento.
@Composable
fun PatchBadge(
    name: String,
    @DrawableRes icon: Int,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val description = if (unlocked) name else stringResource(R.string.profile_patch_locked, name)
    Column(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Image(
            painter = painterResource(if (unlocked) icon else R.drawable.patch_bloqueado),
            contentDescription = null,
            modifier = Modifier.size(76.dp),
        )
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Normal,
            color = if (unlocked) SombraColors.Text else SombraColors.TextMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PatchBadgePreview() {
    SombraTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PatchBadge(name = "Primeiro protetor", icon = R.drawable.patch_primeiro_protetor, unlocked = true)
            PatchBadge(name = "UV 11+", icon = R.drawable.patch_uv_extremo, unlocked = false)
        }
    }
}
