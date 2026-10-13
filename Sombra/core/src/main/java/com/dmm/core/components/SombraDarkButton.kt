package com.dmm.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Botão principal preto (ex.: "Apliquei protetor agora")
@Composable
fun SombraDarkButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(64.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = SombraColors.Text,
            contentColor = Color.White,
        ),
    ) {
        SombraButtonLabel(text)
    }
}

@Preview(showBackground = true)
@Composable
private fun SombraDarkButtonPreview() {
    SombraTheme {
        SombraDarkButton(text = "Apliquei protetor agora", onClick = {}, modifier = Modifier.padding(20.dp))
    }
}
