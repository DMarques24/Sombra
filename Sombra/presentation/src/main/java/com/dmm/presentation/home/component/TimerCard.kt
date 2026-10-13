package com.dmm.presentation.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.home.TimerCardUiState

@Composable
fun TimerCard(
    state: TimerCardUiState,
    onAction: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        TimerCardUiState.Hidden -> Unit
        TimerCardUiState.Idle -> TimerCardRow(
            progress = 0f,
            ringColor = SombraColors.Line,
            title = stringResource(R.string.timer_card_idle_title),
            subtitle = stringResource(R.string.timer_card_idle_subtitle),
            actionText = stringResource(R.string.timer_card_apply_button),
            actionStyle = TimerActionStyle.Dark,
            onAction = onAction,
            onOpen = onOpen,
            modifier = modifier,
        )
        is TimerCardUiState.Running -> TimerCardRow(
            progress = state.progress,
            ringColor = SombraColors.SunDeep,
            title = stringResource(R.string.timer_card_running_title, state.remainingText()),
            subtitle = stringResource(R.string.timer_card_running_subtitle, state.spf, state.appliedAt),
            actionText = stringResource(R.string.reapply_button),
            actionStyle = TimerActionStyle.Outlined,
            onAction = onAction,
            onOpen = onOpen,
            modifier = modifier,
        )
        is TimerCardUiState.Finished -> TimerCardRow(
            progress = 1f,
            ringColor = SombraColors.UvVeryHigh,
            titleColor = SombraColors.UvVeryHigh,
            title = stringResource(R.string.timer_title_finished),
            subtitle = stringResource(R.string.timer_card_finished_subtitle, state.overdueMinutes, state.uvNow),
            actionText = stringResource(R.string.reapply_button),
            actionStyle = TimerActionStyle.Alert,
            onAction = onAction,
            onOpen = onOpen,
            modifier = modifier,
        )
    }
}

private enum class TimerActionStyle { Dark, Outlined, Alert }

@Composable
private fun TimerCardUiState.Running.remainingText(): String =
    if (remainingHours > 0) {
        stringResource(R.string.timer_card_remaining_hours, remainingHours, remainingMinutes)
    } else {
        stringResource(R.string.timer_card_remaining_minutes, remainingMinutes)
    }

@Composable
private fun TimerCardRow(
    progress: Float,
    ringColor: Color,
    title: String,
    subtitle: String,
    actionText: String,
    actionStyle: TimerActionStyle,
    onAction: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    titleColor: Color = SombraColors.Text,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),                 // tocar no cartão abre o ecrã do protetor
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(56.dp),
            color = ringColor,
            trackColor = SombraColors.Line,
            strokeWidth = 7.dp,
            strokeCap = StrokeCap.Butt,
            gapSize = 0.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SombraColors.TextMuted,
            )
        }
        TimerActionButton(text = actionText, style = actionStyle, onClick = onAction)
    }
}

@Composable
private fun TimerActionButton(text: String, style: TimerActionStyle, onClick: () -> Unit) {
    val padding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
    val label = @Composable {
        Text(text = text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
    when (style) {
        TimerActionStyle.Outlined -> OutlinedButton(
            onClick = onClick,
            shape = CircleShape,
            border = BorderStroke(2.dp, SombraColors.Text),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SombraColors.Text),
            contentPadding = padding,
        ) { label() }
        TimerActionStyle.Dark, TimerActionStyle.Alert -> Button(
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (style == TimerActionStyle.Dark) SombraColors.Text else SombraColors.UvVeryHigh,
                contentColor = Color.White,
            ),
            contentPadding = padding,
        ) { label() }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerCardPreview() {
    SombraTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            TimerCard(state = TimerCardUiState.Idle, onAction = {}, onOpen = {})
            TimerCard(
                state = TimerCardUiState.Running(
                    remainingHours = 1, remainingMinutes = 12,
                    spf = 50, appliedAt = "12:08", progress = 0.6f,
                ),
                onAction = {}, onOpen = {},
            )
            TimerCard(state = TimerCardUiState.Finished(overdueMinutes = 6, uvNow = 7), onAction = {}, onOpen = {})
        }
    }
}
