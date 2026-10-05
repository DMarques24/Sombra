package com.dmm.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

@Composable
fun SombraSecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(64.dp),
        shape = CircleShape,
        border = BorderStroke(2.dp, SombraColors.Line),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SombraColors.Text),
    ) {
        SombraButtonLabel(text)
    }
}

@Preview(showBackground = true)
@Composable
private fun SombraSecondaryButtonPreview() {
    SombraTheme {
        SombraSecondaryButton(text = "Já saí do sol", onClick = {}, modifier = Modifier.padding(20.dp))
    }
}
