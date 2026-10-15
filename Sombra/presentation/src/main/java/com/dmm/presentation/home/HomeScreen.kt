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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.dmm.presentation.home.mapper.labelRes
import com.dmm.presentation.home.mapper.onColor
import com.dmm.domain.weather.model.HourRange

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
                title = uiState.locationName ?: stringResource(R.string.home_location_current),
                subtitle = stringResource(R.string.home_today_at, uiState.updatedAt),
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
                contentDescription = stringResource(weather.condition.labelRes),
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
                text = stringResource(R.string.home_condition_feels_like, stringResource(weather.condition.labelRes), weather.feelsLike),
                modifier = Modifier.offset(y = (-16).dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SombraColors.OnSky,
            )
            Spacer(Modifier.height(4.dp))
            TempDetailsComponent(
                uvNumber = stringResource(R.string.home_uv_value, weather.uvIndex),
                description = weather.uvUntilHour
                    ?.let { stringResource(R.string.home_uv_until, stringResource(weather.uvLevel.labelRes), it) }
                    ?: stringResource(weather.uvLevel.labelRes),
                uvColor = weather.uvLevel.color,
                onUvColor = weather.uvLevel.onColor,
            )
        },
        card = {
            if (uiState.locationPrompt != LocationPrompt.None) {
                LocationPromptCard(
                    cityName = uiState.locationName ?: stringResource(R.string.home_location_current),
                    buttonText = stringResource(
                        if (uiState.locationPrompt == LocationPrompt.OpenSettings) R.string.home_location_open_settings
                        else R.string.home_location_allow
                    ),
                    onClick = onLocationPromptClick,
                )
            }
            SunRiskCard(risk = uiState.risk)
            if (uiState.showTimerCard) {
                TimerCard(
                    state = uiState.timerCard,
                    onAction = onTimerAction,
                    onOpen = onOpenTimer,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }
        },
    )
}

// "Queimas em ~21 min sem protetor · Pele tipo II. Procura sombra entre as 12h e as 16h."
@Composable
private fun SunRiskCard(risk: SunRiskUiState) {
    val title = when {
        risk.burnMinutes == null -> stringResource(R.string.home_risk_low_title)        // UV < 1: sem risco
        risk.isBeyondCap -> stringResource(R.string.home_risk_burn_title_cap, SunRiskUiState.BURN_MINUTES_CAP)
        else -> stringResource(R.string.home_risk_burn_title, risk.burnMinutes)
    }
    val description = when {
        risk.shadeHours != null -> risk.shadeHours.text(R.string.home_risk_shade, risk.skinType)
        risk.protectHours != null -> risk.protectHours.text(R.string.home_risk_protect, risk.skinType)
        else -> stringResource(R.string.home_risk_none, risk.skinType)
    }
    InfoComponent(
        title = title,
        description = description,
        backgroundColor = if (risk.isHigh) SombraColors.RiskHighContainer else SombraColors.SkyBluePale,
        titleColor = if (risk.isHigh) SombraColors.OnRiskHighContainer else SombraColors.SkyBlueDeep,
        descriptionColor = if (risk.isHigh) SombraColors.OnRiskHighContainer else SombraColors.SkyBlueDeep,
        iconColor = if (risk.isHigh) SombraColors.UvVeryHigh else SombraColors.SkyBlue,
    )
}

@Composable
private fun HourRange.text(resId: Int, skinType: String): String =
    stringResource(resId, skinType, startHour, endHour)

@Composable
fun HomeLoading(modifier: Modifier = Modifier) {
    HomeLayout(
        modifier = modifier,
        sky = { HomeSkySkeleton() },
        card = { HomeCardSkeleton() },
    )
}

@Composable
internal fun HomeError(
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
                    uvIndex = 8, uvLevel = UvLevel.VeryHigh, uvUntilHour = 16,
                ),
                risk = SunRiskUiState(
                    burnMinutes = 21, skinType = "II",
                    shadeHours = HourRange(12, 16), protectHours = HourRange(10, 18),
                    recommendedSpf = 50,
                ),
                timerCard = TimerCardUiState.Running(
                    remainingHours = 1, remainingMinutes = 12,
                    spf = 50, appliedAt = "12:08", progress = 0.6f,
                ),
                isOffline = true,
                hoursSinceUpdate = 2,
                updatedAt = "13:20",
                locationName = "Coimbra",
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