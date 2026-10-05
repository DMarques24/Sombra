package com.dmm.presentation.register

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Só guarda o que se escreve, para os campos funcionarem.
    // A força da palavra-passe, a localização e o registo vêm depois.
    fun onNameChange(name: String) = _uiState.update { it.copy(name = name) }

    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email) }

    fun onPasswordChange(password: String) = _uiState.update { it.copy(password = password) }

    fun onCityChange(city: String) = _uiState.update { it.copy(city = city) }
}
