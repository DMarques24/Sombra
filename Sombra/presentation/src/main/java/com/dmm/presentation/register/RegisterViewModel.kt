package com.dmm.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.domain.location.usecase.GetCityNameUseCase
import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.model.SignUpOutcome
import com.dmm.domain.auth.usecase.SignUpUseCase
import com.dmm.presentation.common.messageRes
import com.dmm.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Cria a conta no Supabase. A palavra-passe só vai para o Supabase: nunca fica no telemóvel.
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUp: SignUpUseCase,
    private val getCityName: GetCityNameUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Evento de uma só vez: a conta ficou com sessão, o ecrã segue para o tipo de pele
    private val _accountCreated = Channel<Unit>(Channel.BUFFERED)
    val accountCreated: Flow<Unit> = _accountCreated.receiveAsFlow()

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name, message = null) }

    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email, message = null) }

    fun onPasswordChange(password: String) = _uiState.update {
        it.copy(password = password, passwordStrength = passwordStrength(password), message = null)
    }

    fun onCityChange(city: String) = _uiState.update { it.copy(city = city, message = null) }

    // Chamado depois de a pessoa dar (ou já ter) a permissão de localização
    fun onUseLocation() {
        _uiState.update { it.copy(isLocating = true, message = null) }
        viewModelScope.launch {
            val city = getCityName()
            _uiState.update {
                if (city != null) it.copy(city = city, isLocating = false)
                else it.copy(isLocating = false, message = R.string.register_location_not_found, messageIsError = true)
            }
        }
    }

    fun onLocationDenied() = _uiState.update {
        it.copy(message = R.string.register_location_denied, messageIsError = true)
    }

    // O evento só sai quando a conta fica com sessão; se o Supabase pedir confirmação por email,
    // fica neste ecrã com o aviso (depois de confirmar, a pessoa entra pelo Login)
    fun onContinue() {
        val state = _uiState.value
        if (state.isLoading) return
        val problem = when {
            state.name.isBlank() -> R.string.register_name_required
            !EMAIL_REGEX.matches(state.email.trim()) -> R.string.auth_error_invalid_email
            state.password.length < MIN_PASSWORD_LENGTH -> R.string.register_password_short
            else -> null
        }
        if (problem != null) {
            _uiState.update { it.copy(message = problem, messageIsError = true) }
            return
        }
        _uiState.update { it.copy(isLoading = true, message = null) }
        viewModelScope.launch {
            val result = signUp(name = state.name, email = state.email, password = state.password, city = state.city)
            when (result) {
                is AuthResult.Success -> when (result.data) {
                    is SignUpOutcome.SignedIn -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _accountCreated.send(Unit)
                    }
                    SignUpOutcome.ConfirmEmail -> _uiState.update {
                        it.copy(isLoading = false, message = R.string.register_confirm_email, messageIsError = false)
                    }
                }
                is AuthResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, message = result.error.messageRes, messageIsError = true)
                }
            }
        }
    }

    // Fraca: menos de 8 caracteres. Forte: 10+ com letras, números e outro tipo (maiúscula ou símbolo).
    private fun passwordStrength(password: String): PasswordStrength? {
        if (password.isEmpty()) return null
        val kinds = listOf(
            password.any { it.isLowerCase() },
            password.any { it.isUpperCase() },
            password.any { it.isDigit() },
            password.any { !it.isLetterOrDigit() },
        ).count { it }
        return when {
            password.length < 8 || kinds < 2 -> PasswordStrength.WEAK
            password.length >= 10 && kinds >= 3 -> PasswordStrength.STRONG
            else -> PasswordStrength.MEDIUM
        }
    }

    private companion object {
        // O mínimo por defeito do Supabase
        const val MIN_PASSWORD_LENGTH = 6
        val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}
