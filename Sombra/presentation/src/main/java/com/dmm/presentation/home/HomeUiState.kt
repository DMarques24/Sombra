package com.dmm.presentation.home

data class HomeUiState(
    val timerCard: TimerCardUiState = TimerCardUiState.Hidden,
)

sealed interface TimerCardUiState {
    data object Hidden : TimerCardUiState
    data object Idle : TimerCardUiState
    data class Running(
        val remainingHours: Int,
        val remainingMinutes: Int,
        val spf: Int,
        val appliedAt: String,
        val progress: Float,
    ) : TimerCardUiState

    data class Finished(val overdueMinutes: Int, val uvNow: Int) : TimerCardUiState
}
