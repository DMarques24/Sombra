package com.dmm.presentation.forecast.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R

@Composable
fun ForecastTopBar(
    selectedDay: String,
    onSelectDayClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.forecast_title),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = SombraColors.Text,
        )
        OutlinedButton(
            onClick = onSelectDayClick,
            border = BorderStroke(2.dp, SombraColors.Text),
            contentPadding = PaddingValues(start = 18.dp, end = 10.dp),
        ) {
            Text(
                text = selectedDay,
                style = MaterialTheme.typography.titleMedium,
                color = SombraColors.Text,
            )
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                tint = SombraColors.Text,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastTopBarPreview() {
    SombraTheme {
        ForecastTopBar(
            selectedDay = stringResource(R.string.forecast_today),
            onSelectDayClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
