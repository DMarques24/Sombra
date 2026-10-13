package com.dmm.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R

@Composable
fun OfflineBanner(
    hoursSinceUpdate: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(SombraColors.Text, CircleShape)
            .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.WifiOff,
            contentDescription = null,                    // o texto ao lado já explica
            tint = Color.White,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = stringResource(R.string.home_offline, hoursSinceUpdate),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
        // Texto clicável em vez de TextButton: o TextButton tem 40dp de altura mínima
        // e deixava a pílula demasiado alta
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(enabled = !isRefreshing, role = Role.Button, onClick = onRefresh)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.home_offline_refresh),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                // enquanto atualiza, o texto fica invisível mas guarda o espaço (a pílula não muda de tamanho)
                color = if (isRefreshing) Color.Transparent else SombraColors.SunStrong,
            )
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = SombraColors.SunStrong,
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}

@Preview
@Composable
private fun OfflineBannerPreview() {
    SombraTheme {
        OfflineBanner(hoursSinceUpdate = 2, isRefreshing = false, onRefresh = {})
    }
}

@Preview
@Composable
private fun OfflineBannerRefreshingPreview() {
    SombraTheme {
        OfflineBanner(hoursSinceUpdate = 2, isRefreshing = true, onRefresh = {})
    }
}
