package com.dmm.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import com.dmm.domain.sunscreen.usecase.ObserveTimerStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @RequiresApi(Build.VERSION_CODES.O)
@Inject constructor(
    observeTimerStatus: ObserveTimerStatusUseCase,
    private val sunscreenRepository: SunscreenRepository,
) : ViewModel() {

    // Cada vez que muda, o flow de baixo recomeça do zero (é assim que o "Tenta outra vez" funciona)
    private val retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<HomeUiState> =
        retryTrigger
            .flatMapLatest {
                observeTimerStatus()
                    .map<_, HomeUiState> { status -> HomeUiState.Content(timerCard = status.toCard(uvNow = 8)) }
                    .onStart { emit(HomeUiState.Loading) }
                    .catch { emit(HomeUiState.Error) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)

    // Ecrã de erro: volta ao esqueleto e tenta carregar tudo outra vez
    fun onRetry() {
        retryTrigger.value++
    }

    fun onRefresh() {
        // TODO: quando houver repositório do tempo, pedir os dados outra vez aqui
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onTimerAction() {
        val content = uiState.value as? HomeUiState.Content ?: return
        viewModelScope.launch {
            when (content.timerCard) {
                TimerCardUiState.Idle -> sunscreenRepository.start(spf = 50, inWater = false)
                is TimerCardUiState.Running,
                is TimerCardUiState.Finished -> sunscreenRepository.reapply()
            }
        }
    }
}
