package com.dmm.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.repository.SunscreenRepository
import com.dmm.domain.usecase.ObserveTimerStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @RequiresApi(Build.VERSION_CODES.O)
@Inject constructor(
    observeTimerStatus: ObserveTimerStatusUseCase,
    private val sunscreenRepository: SunscreenRepository,
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<HomeUiState> =
        observeTimerStatus()
            .map { status -> HomeUiState(timerCard = status.toCard(uvNow = 8)) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    @RequiresApi(Build.VERSION_CODES.O)
    fun onTimerAction() {
        viewModelScope.launch {
            when (uiState.value.timerCard) {
                TimerCardUiState.Idle -> sunscreenRepository.start(spf = 50, inWater = false)
                is TimerCardUiState.Running,
                is TimerCardUiState.Finished -> sunscreenRepository.reapply()
                TimerCardUiState.Hidden -> Unit
            }
        }
    }
}
