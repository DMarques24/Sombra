package com.dmm.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme


@Composable
fun SombraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(20.dp)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = SombraColors.TextMuted,
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            interactionSource = interactionSource,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = SombraColors.Text),
            cursorBrush = SolidColor(SombraColors.Text),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) keyboardOptions.copy(keyboardType = KeyboardType.Password) else keyboardOptions,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(if (focused) Color.White else SombraColors.SurfaceSoft, shape)
                        .border(2.dp, if (focused) SombraColors.Text else Color.Transparent, shape)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1f)) { innerTextField() }
                    trailing?.invoke()
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SombraTextFieldPreview() {
    SombraTheme {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SombraTextField(value = "diogo@exemplo.pt", onValueChange = {}, label = "Email")
            SombraTextField(
                value = "palavrapasse",
                onValueChange = {},
                label = "Palavra-passe",
                isPassword = true,
                trailing = { Text("Esqueci-me", color = SombraColors.TextMuted) },
            )
        }
    }
}
