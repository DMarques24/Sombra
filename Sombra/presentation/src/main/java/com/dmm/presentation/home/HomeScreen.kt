package com.dmm.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.components.SombraTopBar
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.domain.common.DataError
import com.dmm.presentation.common.descriptionRes
import com.dmm.presentation.common.titleRes
import com.dmm.domain.weather.model.UvLevel
import com.dmm.domain.weather.model.WeatherCondition
import com.dmm.presentation.R
import com.dmm.presentation.home.component.TempDetailsComponent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.dmm.core.components.LocationPermissionStatus
import com.dmm.core.components.SombraDarkButton
import com.dmm.core.components.hasLocationPermission
import com.dmm.core.components.openAppSettings
import com.dmm.core.components.rememberLocationPermissionRequest
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.dmm.presentation.home.component.LocationPromptCard
import com.dmm.presentation.home.component.HomeCardSkeleton
import com.dmm.presentation.home.component.HomeSkySkeleton
import com.dmm.presentation.home.component.InfoComponent
import com.dmm.presentation.home.component.OfflineBanner
import com.dmm.presentation.home.component.TimerCard
import com.dmm.presentation.home.mapper.color
import com.dmm.presentation.home.mapper.imageRes
import com.dmm.presentation.home.mapper.label
import com.dmm.presentation.home.mapper.onColor

@Composable
fun HomeScreen(
    onOpenTimer: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val requestPermission = rememberLocationPermissionRequest { status ->
        viewModel.onLocationPermissionResult(status)
    }
    // Corre ao abrir o ecrã e sempre que a app volta ao primeiro plano:
    // assim, se a pessoa ativou a localização nas definições, o tempo atualiza sozinho
    LifecycleResumeEffect(Unit) {
        when {
            context.hasLocationPermission() ->
                viewModel.onLocationPermissionResult(LocationPermissionStatus.Granted)
            viewModel.shouldAskForLocation() -> requestPermission()
        }
        onPauseOrDispose { }
    }

    AnimatedContent(
        targetState = uiState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentKey = { it::class },
        label = "home",
    ) { state ->
        when (state) {
            HomeUiState.Loading -> HomeLoading()
            is HomeUiState.Content -> HomeContent(
                uiState = state,
                onTimerAction = viewModel::onTimerAction,
                onRefresh = viewModel::onRefresh,
                onOpenTimer = onOpenTimer,
                onLocationPromptClick = {
                    when (state.locationPrompt) {
                        LocationPrompt.AskAgain -> requestPermission()
                        LocationPrompt.OpenSettings -> context.openAppSettings()
                        LocationPrompt.None -> Unit
                    }
                },
            )

            is HomeUiState.Error -> HomeError(error = state.error, onRetry = viewModel::onRetry)
        }
    }
}

@Composable
fun HomeContent(
    uiState: HomeUiState.Content,
    onTimerAction: () -> Unit,
    onRefresh: () -> Unit,
    onOpenTimer: () -> Unit,
    modifier: Modifier = Modifier,
    onLocationPromptClick: () -> Unit = {},
) {
    HomeLayout(
        modifier = modifier,
        sky = {
            SombraTopBar(
                title = if (uiState.isDefaultLocation) "Coimbra" else stringResource(R.string.home_location_current),
                subtitle = "Hoje, 13:20",
                onNotificationsClick = { /* TODO: abrir notificações */ },
            )
            AnimatedVisibility(visible = uiState.isOffline) {      // aparece e desaparece suavemente
                OfflineBanner(
                    hoursSinceUpdate = uiState.hoursSinceUpdate,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            val weather = uiState.weather
            Image(
                painter = painterResource(weather.imageRes()),
                contentDescription = weather.condition.label,
                modifier = Modifier.size(230.dp),
            )
            Text(
                text = "${weather.temperature}°",
                modifier = Modifier.offset(y = (-24).dp),
                style = TextStyle(
                    fontSize = 96.sp,
                    lineHeight = 96.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp,
                ),
                color = SombraColors.OnSky,
            )
            Text(
                text = "${weather.condition.label} · sensação ${weather.feelsLike}°",
                modifier = Modifier.offset(y = (-16).dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SombraColors.OnSky,
            )
            Spacer(Modifier.height(4.dp))
            TempDetailsComponent(
                uvNumber = "UV ${weather.uvIndex}",
                description = weather.uvLevel.label,
                uvColor = weather.uvLevel.color,
                onUvColor = weather.uvLevel.onColor,
            )
        },
        card = {
            if (uiState.locationPrompt != LocationPrompt.None) {
                LocationPromptCard(
                    buttonText = stringResource(
                        if (uiState.locationPrompt == LocationPrompt.OpenSettings) R.string.home_location_open_settings
                        else R.string.home_location_allow
                    ),
                    onClick = onLocationPromptClick,
                )
            }
            InfoComponent(
                title = "Queimas em ~15 min sem protetor",
                description = "Pele tipo II. Procura sombra entre as 12h e as 16h.",
                backgroundColor = SombraColors.RiskHighContainer,
                titleColor = SombraColors.OnRiskHighContainer,
                descriptionColor = SombraColors.OnRiskHighContainer,
            )
            TimerCard(
                state = uiState.timerCard,
                onAction = onTimerAction,
                onOpen = onOpenTimer,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
        },
    )
}

@Composable
fun HomeLoading(modifier: Modifier = Modifier) {
    HomeLayout(
        modifier = modifier,
        sky = { HomeSkySkeleton() },
        card = { HomeCardSkeleton() },
    )
}

@Composable
private fun HomeError(
    error: DataError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.HomeGround)
            .statusBarsPadding()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ilustracao_offline),
                contentDescription = null,
                modifier = Modifier.size(160.dp),
            )
            Text(
                text = stringResource(error.titleRes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = SombraColors.Text,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(error.descriptionRes) + " " + stringResource(R.string.home_error_timer_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = SombraColors.TextMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            SombraDarkButton(
                text = stringResource(R.string.action_retry),
                onClick = onRetry,
            )
        }
    }
}

@Composable
private fun HomeLayout(
    sky: @Composable ColumnScope.() -> Unit,
    card: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.HomeGround)
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(SombraColors.SkySunny))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp + 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = sky,
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-64).dp)
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp))
                .background(SombraColors.Surface, RoundedCornerShape(28.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = card,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    SombraTheme {
        HomeContent(
            uiState = HomeUiState.Content(
                weather = HomeWeatherUiState(
                    temperature = 27, feelsLike = 28,
                    condition = WeatherCondition.Clear, isDay = true,
                    uvIndex = 8, uvLevel = UvLevel.VeryHigh,
                ),
                timerCard = TimerCardUiState.Running(
                    remainingHours = 1, remainingMinutes = 12,
                    spf = 50, appliedAt = "12:08", progress = 0.6f,
                ),
                isOffline = true,
                hoursSinceUpdate = 2,
            ),
            onTimerAction = {},
            onRefresh = {},
            onOpenTimer = {},
        )
    }
}

@Preview(showBackground = true, name = "Início · a carregar")
@Composable
private fun HomeLoadingPreview() {
    SombraTheme { HomeLoading() }
}

@Preview(showBackground = true, name = "Início · erro")
@Composable
private fun HomeErrorPreview() {
    SombraTheme { HomeError(error = DataError.NoConnection, onRetry = {}) }
}