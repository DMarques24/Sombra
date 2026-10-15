package com.dmm.presentation.login

import androidx.annotation.StringRes

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    @param:StringRes val message: Int? = null,      // erro ou aviso por baixo dos campos
    val messageIsError: Boolean = true,             // false: é uma boa notícia (ex.: email enviado)
)
