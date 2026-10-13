package com.dmm.presentation.profile.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun ProfileHeader(name: String, subtitle: String, level: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileAvatar(initial = name.take(1).uppercase(), level = level)
        Spacer(Modifier.height(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = SombraColors.Text,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = SombraColors.TextMuted,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileHeaderPreview() {
    SombraTheme {
        ProfileHeader(name = "Diogo", subtitle = "Pele tipo II · Coimbra", level = 3, modifier = Modifier.padding(20.dp))
    }
}
