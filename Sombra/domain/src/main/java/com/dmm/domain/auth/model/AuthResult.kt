package com.dmm.domain.auth.model

sealed interface AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>
    data class Failure(val error: AuthError) : AuthResult<Nothing>
}

// O registo pode acabar de duas formas: já com sessão, ou à espera da confirmação por email
sealed interface SignUpOutcome {
    data class SignedIn(val user: AuthUser) : SignUpOutcome
    data object ConfirmEmail : SignUpOutcome
}
