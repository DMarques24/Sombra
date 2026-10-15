package com.dmm.presentation.forecast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.dmm.core.components.SombraDarkButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.Umbrella
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.dmm.domain.common.DataError
import com.dmm.domain.weather.model.WeatherCondition
import com.dmm.presentation.common.descriptionRes
import com.dmm.presentation.common.titleRes
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.domain.weather.model.HourRange
import java.time.LocalDate
import com.dmm.presentation.forecast.component.ForecastSkeleton
import com.dmm.presentation.forecast.component.ForecastTopBar
import com.dmm.presentation.forecast.component.HourlyUv
import com.dmm.presentation.forecast.component.HourlyUvCard
import com.dmm.presentation.forecast.component.InfoCard
import com.dmm.presentation.forecast.component.UvDayCard


@Composable
fun ForecastScreen(viewModel: ForecastViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Como no Início: o esqueleto desaparece suavemente quando os dados chegam.
    // A chave é a classe, por isso trocar de dia (Content -> Content) não volta a animar.
    AnimatedContent(
        targetState = uiState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentKey = { it::class },
        label = "forecast",
    ) { state ->
        when (state) {
            ForecastUiState.Loading -> ForecastSkeleton()
            is ForecastUiState.Error -> ForecastError(error = state.error, onRetry = viewModel::onRetry)
            is ForecastUiState.Content -> ForecastContent(state, onSelectDay = viewModel::onSelectDay)
        }
    }
}

@Composable
private fun ForecastError(error: DataError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(error.titleRes),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = SombraColors.Text,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(error.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            color = SombraColors.TextMuted,
            textAlign = TextAlign.Center,
        )
        SombraDarkButton(
            text = stringResource(R.string.action_retry),
            onClick = onRetry,
        )
    }
}

@Composable
fun ForecastContent(
    uiState: ForecastUiState.Content,
    modifier: Modifier = Modifier,
    onSelectDay: (LocalDate) -> Unit = {},
) {
    var isDayMenuOpen by remember { mutableStateOf(false) }
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
        // O menu abre por baixo do botão do dia (fica no canto direito da Box)
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            ForecastTopBar(
                selectedDay = uiState.selectedDay.label,
                onSelectDayClick = { isDayMenuOpen = true },
            )
            Box(Modifier.align(Alignment.BottomEnd)) {
                DropdownMenu(expanded = isDayMenuOpen, onDismissRequest = { isDayMenuOpen = false }) {
                    uiState.dayOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                isDayMenuOpen = false
                                onSelectDay(option.date)
                            },
                        )
                    }
                }
            }
        }
        if (uiState.isOffline) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.forecast_offline),
                style = MaterialTheme.typography.bodyMedium,
                color = SombraColors.TextMuted,
            )
        }
        Spacer(Modifier.height(20.dp))
        HourlyUvCard(
            hourly = uiState.hourly,
            currentHour = uiState.currentHour,
            summary = stringResource(
                if (uiState.isToday) R.string.forecast_uv_now else R.string.forecast_uv_max,
                uiState.currentUv,
            ),
        )
        Spacer(Modifier.height(16.dp))
        InfoCard(
            title = stringResource(
                if (uiState.bestTime.forRunning) R.string.forecast_best_time_run else R.string.forecast_best_time_out,
            ),
            description = uiState.bestTime.uvHours
                ?.let { stringResource(R.string.forecast_best_time_range, it.startHour, it.endHour) }
                ?: stringResource(R.string.forecast_best_time_all_day),
        )
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
                .padding(horizontal = 20.dp, vertical = 6.dp),
        ) {
            uiState.days.forEachIndexed { index, day ->
                if (index > 0) HorizontalDivider(color = SombraColors.Line)
                UvDayCard(day = day.label, uvLevel = day.uvIndex, icon = day.condition.icon)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastContentPreview() {
    SombraTheme {
        ForecastContent(
            uiState = ForecastUiState.Content(
                hourly = listOf(1, 2, 4, 6, 8, 9, 9, 7, 5, 3, 2, 1)
                    .mapIndexed { i, uv -> HourlyUv(hour = 8 + i, uvIndex = uv) },
                currentHour = 13,
                currentUv = 8,
                selectedDay = ForecastDayOption(LocalDate.of(2026, 7, 20), "Hoje"),
                dayOptions = emptyList(),
                bestTime = BestTimeUiState(forRunning = true, uvHours = HourRange(10, 17)),
                days = listOf(
                    ForecastDayUiState("Amanhã", uvIndex = 8, condition = WeatherCondition.Clear),
                    ForecastDayUiState("Sáb", uvIndex = 5, condition = WeatherCondition.Cloudy),
                    ForecastDayUiState("Dom", uvIndex = 7, condition = WeatherCondition.Clear),
                ),
            ),
        )
    }
}

private val WeatherCondition.icon: ImageVector
    get() = when (this) {
        WeatherCondition.Clear, WeatherCondition.PartlyCloudy -> Icons.Rounded.WbSunny
        WeatherCondition.Cloudy, WeatherCondition.Fog -> Icons.Rounded.Cloud
        WeatherCondition.Rain -> Icons.Rounded.Umbrella
        WeatherCondition.Snow -> Icons.Rounded.AcUnit
        WeatherCondition.Storm -> Icons.Rounded.Thunderstorm
    }
