package com.dmm.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun SombraSunButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // O Button não aceita gradientes: fica transparente e o gradiente vai no fundo
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Brush.horizontalGradient(SombraColors.SunButton), CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = SombraColors.Ink,
        ),
        contentPadding = PaddingValues(0.dp),
    ) {
        SombraButtonLabel(text, Modifier.fillMaxSize().wrapContentSize())
    }
}

@Preview(showBackground = true)
@Composable
private fun SombraSunButtonPreview() {
    SombraTheme {
        SombraSunButton(text = "Reapliquei agora", onClick = {}, modifier = Modifier.padding(20.dp))
    }
}
