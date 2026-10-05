package com.dmm.presentation.forecast

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ForecastUiState())

    val uiState: StateFlow<ForecastUiState> = _uiState.asStateFlow()

}