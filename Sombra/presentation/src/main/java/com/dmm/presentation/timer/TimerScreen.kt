package com.dmm.presentation.timer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmm.core.components.SombraDarkButton
import com.dmm.core.components.SombraSecondaryButton
import com.dmm.core.components.SombraSunButton
import com.dmm.core.components.rememberNotificationPermissionRequest
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.common.spfLabel
import com.dmm.presentation.home.component.InfoComponent
import com.dmm.presentation.timer.component.SpfSelector
import com.dmm.presentation.timer.component.TimerButtonSkeleton
import com.dmm.presentation.timer.component.TimerContentSkeleton
import com.dmm.presentation.timer.component.TimerHeader
import com.dmm.presentation.timer.component.TimerRing
import com.dmm.presentation.timer.component.TimerStatsCard
import com.dmm.presentation.timer.component.WaterToggleCard

@Composable
fun TimerScreen(viewModel: TimerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // O aviso "Hora de reaplicar" é uma notificação: pede autorização ao começar o temporizador
    val requestNotifications = rememberNotificationPermissionRequest()
    TimerContent(
        uiState = uiState,
        onSelectSpf = viewModel::onSelectSpf,
        onWaterChange = viewModel::onWaterChange,
        onApply = {
            requestNotifications()
            viewModel.onApply()
        },
        onReapply = viewModel::onReapply,
        onLeftSun = viewModel::onLeftSun,
    )
}

@Composable
fun TimerContent(
    uiState: TimerUiState,
    modifier: Modifier = Modifier,
    onSelectSpf: (Int) -> Unit = {},
    onWaterChange: (Boolean) -> Unit = {},
    onApply: () -> Unit = {},
    onReapply: () -> Unit = {},
    onLeftSun: () -> Unit = {},
) {
    // Como no Início: troca com um fade entre o esqueleto e cada um dos estados
    AnimatedContent(
        targetState = uiState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentKey = { it::class },
        label = "timer",
    ) { state ->
        TimerState(state, modifier, onSelectSpf, onWaterChange, onApply, onReapply, onLeftSun)
    }
}

@Composable
private fun TimerState(
    uiState: TimerUiState,
    modifier: Modifier,
    onSelectSpf: (Int) -> Unit,
    onWaterChange: (Boolean) -> Unit,
    onApply: () -> Unit,
    onReapply: () -> Unit,
    onLeftSun: () -> Unit,
) {
    when (uiState) {
        TimerUiState.Loading -> TimerLayout(
            modifier = modifier,
            content = { TimerContentSkeleton() },
            buttons = { TimerButtonSkeleton() },
        )

        is TimerUiState.Idle -> TimerLayout(
            modifier = modifier,
            content = { IdleContent(uiState, onSelectSpf, onWaterChange) },
            buttons = {
                SombraDarkButton(
                    text = stringResource(R.string.timer_apply_button),
                    onClick = onApply
                )
            },
        )

        is TimerUiState.Running -> TimerLayout(
            modifier = modifier,
            content = { RunningContent(uiState) },
            buttons = {
                SombraSunButton(
                    text = stringResource(R.string.timer_reapply_button),
                    onClick = onReapply
                )
                SombraSecondaryButton(
                    text = stringResource(R.string.timer_left_sun_button),
                    onClick = onLeftSun
                )
            },
        )

        is TimerUiState.Finished -> TimerLayout(
            modifier = modifier,
            content = { FinishedContent(uiState) },
            buttons = {
                SombraSunButton(
                    text = stringResource(
                        R.string.timer_reapply_xp_button,
                        uiState.xpReward
                    ), onClick = onReapply
                )
                SombraSecondaryButton(
                    text = stringResource(R.string.timer_left_sun_button),
                    onClick = onLeftSun
                )
            },
        )
    }
}

// Esqueleto comum aos três estados: conteúdo que faz scroll em cima, botões sempre visíveis em baixo
@Composable
private fun TimerLayout(
    content: @Composable ColumnScope.() -> Unit,
    buttons: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.Surface)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = content,
        )
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = buttons,
        )
    }
}

@Composable
private fun IdleContent(
    state: TimerUiState.Idle,
    onSelectSpf: (Int) -> Unit,
    onWaterChange: (Boolean) -> Unit,
) {
    TimerHeader(
        title = stringResource(R.string.timer_title),
        subtitle = stringResource(R.string.timer_subtitle_idle),
    )
    TimerRing(
        time = state.duration,
        label = stringResource(R.string.timer_ring_idle),
        progress = 0f,
        timeColor = SombraColors.TextMuted,
        size = 180.dp,
    )
    UvWarning(
        title = state.uvNow?.let { stringResource(R.string.timer_uv_now_title, it) }
            ?: stringResource(R.string.timer_uv_unknown_title),
        description = stringResource(R.string.timer_uv_recommendation, spfLabel(state.recommendedSpf)),
    )
    // O seletor trabalha com texto ("50+"); aqui converte-se de volta para o número
    SpfSelector(
        label = stringResource(R.string.timer_spf_label),
        options = state.spfOptions.map(::spfLabel),
        selected = spfLabel(state.selectedSpf),
        onSelect = { label -> onSelectSpf(state.spfOptions.first { spfLabel(it) == label }) },
    )
    WaterToggleCard(
        title = stringResource(R.string.timer_water_title),
        subtitle = stringResource(R.string.timer_water_subtitle),
        checked = state.goingToWater,
        onCheckedChange = onWaterChange,
    )
}

@Composable
private fun RunningContent(state: TimerUiState.Running) {
    TimerHeader(
        title = stringResource(R.string.timer_title),
        subtitle = stringResource(R.string.timer_subtitle_running, spfLabel(state.spf), state.appliedAt),
    )
    TimerRing(
        time = state.remaining,
        label = stringResource(R.string.timer_ring_running),
        progress = state.progress,
    )
    TimerStatsCard(
        stats = listOf(
            stringResource(R.string.timer_stat_notify) to state.notifyAt,
            stringResource(R.string.timer_stat_uv) to (state.uvNow?.toString() ?: "–"),
            stringResource(R.string.timer_stat_water) to stringResource(if (state.inWater) R.string.yes else R.string.no),
        ),
    )
    Hint(stringResource(R.string.timer_notify_hint))
}

@Composable
private fun FinishedContent(state: TimerUiState.Finished) {
    TimerHeader(
        title = stringResource(R.string.timer_title_finished),
        subtitle = stringResource(
            R.string.timer_subtitle_finished,
            elapsedText(state.elapsedMinutes),
            state.appliedAt,
        ),
    )
    TimerRing(
        time = "0:00",
        label = stringResource(R.string.timer_ring_finished, state.finishedMinutesAgo),
        progress = 1f,
        progressBrush = SolidColor(SombraColors.UvVeryHigh),
        timeColor = SombraColors.UvVeryHigh,
        strokeWidth = 16.dp,
    )
    if (state.uvNow != null) {
        UvWarning(
            title = stringResource(R.string.timer_uv_still_title, state.uvNow),
            description = stringResource(R.string.timer_uv_still_description),
        )
    }
    Hint(stringResource(R.string.timer_auto_stop_hint, state.autoStopMinutes))
}

// "2 horas" ou "40 min" (na água o temporizador só tem 40 minutos)
@Composable
private fun elapsedText(minutes: Int): String =
    if (minutes >= 60) pluralStringResource(R.plurals.timer_elapsed_hours, minutes / 60, minutes / 60)
    else stringResource(R.string.timer_elapsed_minutes, minutes)

@Composable
private fun UvWarning(title: String, description: String) {
    InfoComponent(
        title = title,
        description = description,
        backgroundColor = SombraColors.RiskHighContainer,
        titleColor = SombraColors.OnRiskHighContainer,
        descriptionColor = SombraColors.OnRiskHighContainer,
    )
}

@Composable
private fun Hint(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium,
        color = SombraColors.TextMuted,
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true, heightDp = 800, name = "Protetor · a carregar")
@Composable
private fun TimerLoadingPreview() {
    SombraTheme { TimerContent(uiState = TimerUiState.Loading) }
}

@Preview(showBackground = true, heightDp = 800, name = "08 · sem temporizador")
@Composable
private fun TimerIdlePreview() {
    SombraTheme { TimerContent(uiState = TimerUiState.Idle()) }
}

@Preview(showBackground = true, heightDp = 800, name = "09 · a decorrer")
@Composable
private fun TimerRunningPreview() {
    SombraTheme { TimerContent(uiState = TimerUiState.Running()) }
}

@Preview(showBackground = true, heightDp = 800, name = "10 · terminou")
@Composable
private fun TimerFinishedPreview() {
    SombraTheme { TimerContent(uiState = TimerUiState.Finished()) }
}
