package com.dmm.presentation.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.forecast.component.ForecastTopBar
import com.dmm.presentation.forecast.component.HourlyUv
import com.dmm.presentation.forecast.component.HourlyUvCard
import com.dmm.presentation.forecast.component.InfoCard
import com.dmm.presentation.forecast.component.UvDayCard


@Composable
fun ForecastScreen(viewModel: ForecastViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ForecastContent(uiState)
}

@Composable
fun ForecastContent(uiState: ForecastUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ForecastTopBar(
            selectedDay = stringResource(R.string.forecast_today),
            onSelectDayClick = { /* TODO: abrir o seletor de dia */ },
        )
        Spacer(Modifier.height(20.dp))
        // TODO: dados de exemplo, trocar pelos do ForecastUiState quando houver API
        HourlyUvCard(
            hourly = listOf(1, 2, 4, 6, 8, 9, 9, 7, 5, 3, 2, 1)
                .mapIndexed { i, uv -> HourlyUv(hour = 8 + i, uvIndex = uv) },
            currentHour = 13,
            currentUv = 8,
        )
        Spacer(Modifier.height(16.dp))
        InfoCard(
            title = "Melhor hora para correr",
            description = "até ás 10:00 . depois das 17:00",
            modifier = Modifier.padding(20.dp),
        )
        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.height(16.dp))
        // TODO: dados de exemplo, trocar pelos do ForecastUiState quando houver API
        val days = listOf(
            Triple("Amanhã", 8, Icons.Rounded.WbSunny),
            Triple("Sáb", 5, Icons.Rounded.Cloud),
            Triple("Dom", 7, Icons.Rounded.WbSunny),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
                .padding(horizontal = 20.dp, vertical = 6.dp),
        ) {
            days.forEachIndexed { index, (day, uv, icon) ->
                if (index > 0) HorizontalDivider(color = SombraColors.Line)
                UvDayCard(day = day, uvLevel = uv, icon = icon)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastContentPreview() {
    SombraTheme {
        ForecastContent(uiState = ForecastUiState())
    }
}

