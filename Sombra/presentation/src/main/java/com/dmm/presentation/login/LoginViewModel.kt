package com.dmm.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.repository.AuthRepository
import com.dmm.domain.auth.usecase.SignInUseCase
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.presentation.R
import com.dmm.presentation.common.messageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signIn: SignInUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Evento de uma só vez: o ecrã navega quando o recebe (ver ObserveAsEvents)
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events: Flow<LoginEvent> = _events.receiveAsFlow()

    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email, message = null) }

    fun onPasswordChange(password: String) = _uiState.update { it.copy(password = password, message = null) }

    fun onLogin() {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.email.isBlank() || state.password.isEmpty()) {
            _uiState.update { it.copy(message = R.string.login_missing_fields, messageIsError = true) }
            return
        }
        _uiState.update { it.copy(isLoading = true, message = null) }
        viewModelScope.launch {
            when (val result = signIn(state.email, state.password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, password = "") }
                    // Diz se o registo já foi acabado (tipo de pele escolhido): decide para onde ir
                    _events.send(LoginEvent.SignedIn(userRepository.observeProfile().first().onboardingCompleted))
                }
                is AuthResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, message = result.error.messageRes, messageIsError = true)
                }
            }
        }
    }

    // "Esqueci-me": o Supabase manda um email com o link para mudar a palavra-passe
    fun onForgotPassword() {
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(message = R.string.login_forgot_needs_email, messageIsError = true) }
            return
        }
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            _uiState.update {
                when (result) {
                    is AuthResult.Success -> it.copy(message = R.string.login_forgot_sent, messageIsError = false)
                    is AuthResult.Failure -> it.copy(message = result.error.messageRes, messageIsError = true)
                }
            }
        }
    }
}
