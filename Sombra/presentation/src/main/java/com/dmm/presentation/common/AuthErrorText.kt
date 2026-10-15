package com.dmm.presentation.common

import androidx.annotation.StringRes
import com.dmm.domain.auth.model.AuthError
import com.dmm.presentation.R

// A frase que aparece no ecrã para cada erro de autenticação
@get:StringRes
val AuthError.messageRes: Int
    get() = when (this) {
        AuthError.InvalidCredentials -> R.string.auth_error_invalid_credentials
        AuthError.EmailAlreadyUsed -> R.string.auth_error_email_used
        AuthError.WeakPassword -> R.string.auth_error_weak_password
        AuthError.InvalidEmail -> R.string.auth_error_invalid_email
        AuthError.EmailNotConfirmed -> R.string.auth_error_email_not_confirmed
        AuthError.TooManyRequests -> R.string.auth_error_too_many_requests
        AuthError.NoConnection -> R.string.error_no_connection_title
        AuthError.NotConfigured -> R.string.auth_error_not_configured
        AuthError.Unknown -> R.string.auth_error_unknown
    }
