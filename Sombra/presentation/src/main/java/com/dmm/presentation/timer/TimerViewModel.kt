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
}
