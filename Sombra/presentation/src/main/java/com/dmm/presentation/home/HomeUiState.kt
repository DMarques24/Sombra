package com.dmm.presentation.home

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Error : HomeUiState
    data class Content(
        val timerCard: TimerCardUiState,
        val isOffline: Boolean = false,
        val hoursSinceUpdate: Int = 0,
        val updatedAt: String = "13:20",
        val isRefreshing: Boolean = false,
    ) : HomeUiState
}


sealed interface TimerCardUiState {
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
