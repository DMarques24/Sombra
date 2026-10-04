package com.dmm.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun TempDetailsComponent(
    uvNumber: String,
    description: String,
    modifier: Modifier = Modifier,
    uvColor: Color = SombraColors.UvVeryHigh,
    onUvColor: Color = Color.White,
) {
    Row(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = CircleShape)
            .background(Color.White, CircleShape)
            .padding(start = 6.dp, top = 6.dp, end = 16.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .background(uvColor, CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(
                text = uvNumber,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onUvColor,
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SombraColors.Text,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2F8EF0)
@Composable
private fun TempDetailsComponentPreview() {
    SombraTheme {
        TempDetailsComponent(
            uvNumber = "UV 8",
            description = "Muito alto até às 16h",
            modifier = Modifier.padding(16.dp),
        )
    }
}
