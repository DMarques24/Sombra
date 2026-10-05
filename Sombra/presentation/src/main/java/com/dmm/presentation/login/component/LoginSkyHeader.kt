package com.dmm.presentation.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R

@Composable
fun LoginSkyHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(SombraColors.SkyLogin))
            .statusBarsPadding()
            .padding(top = 8.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.weather_sol),
            contentDescription = null,          // decorativo
            modifier = Modifier.size(180.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginSkyHeaderPreview() {
    SombraTheme { LoginSkyHeader() }
}
