package com.dmm.presentation.timer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor() : ViewModel() {
    // Por agora só o estado inicial; a contagem e as notificações vêm depois
    private val _uiState = MutableStateFlow<TimerUiState>(TimerUiState.Idle())

    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    fun onApply() {
        val idle = _uiState.value as? TimerUiState.Idle ?: return
        _uiState.value = TimerUiState.Running(
            spf = idle.selectedSpf.removeSuffix("+").toInt(),
            inWater = idle.goingToWater
        )
    }

    fun onReapply() {
        _uiState.value = TimerUiState.Running()
    }

    fun onLeftSun() {
        _uiState.value = TimerUiState.Idle()
    }
}
