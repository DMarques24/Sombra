package com.dmm.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraTheme

@Composable
fun SombraTopBar(
    title: String,
    subtitle: String,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
            )
        }
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(48.dp)
                .background(contentColor.copy(alpha = 0.25f), CircleShape),
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificações",
                tint = contentColor,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2F8EF0)
@Composable
private fun SombraTopBarPreview() {
    SombraTheme {
        SombraTopBar(
            title = "Coimbra",
            subtitle = "Hoje, 13:20",
            onNotificationsClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
