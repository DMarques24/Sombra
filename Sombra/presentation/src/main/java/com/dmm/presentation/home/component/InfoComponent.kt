package com.dmm.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun InfoComponent(
    title: String,
    description: String,
    backgroundColor: Color,
    titleColor: Color,
    descriptionColor: Color,
    modifier: Modifier = Modifier,
    iconColor: Color = SombraColors.UvVeryHigh,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = descriptionColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoComponentPreview() {
    SombraTheme {
        InfoComponent(
            title = "Queimas em ~15 min sem protetor",
            description = "Pele tipo II. Procura sombra entre as 12h e as 16h.",
            backgroundColor = SombraColors.RiskHighContainer,
            titleColor = SombraColors.OnRiskHighContainer,
            descriptionColor = SombraColors.OnRiskHighContainer,
            modifier = Modifier.padding(16.dp),
        )
    }
}
