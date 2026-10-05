package com.dmm.presentation.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Círculo do sol com a inicial do nome e, no canto, o nível numa bolinha preta.
@Composable
fun ProfileAvatar(initial: String, level: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(124.dp)) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Brush.radialGradient(listOf(SombraColors.Sun, SombraColors.SunStrong, SombraColors.SunDeep)),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                style = TextStyle(fontSize = 52.sp, fontWeight = FontWeight.Black),
                color = SombraColors.Ink,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp)
                .border(3.dp, Color.White, CircleShape)
                .padding(3.dp)
                .background(SombraColors.Text, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = level.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileAvatarPreview() {
    SombraTheme {
        ProfileAvatar(initial = "D", level = 3, modifier = Modifier.padding(20.dp))
    }
}
