package com.dmm.presentation.register

import androidx.annotation.StringRes

enum class PasswordStrength { WEAK, MEDIUM, STRONG }

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val city: String = "",
    val passwordStrength: PasswordStrength? = null,     // null = ainda não há nada para avaliar
    val isLocating: Boolean = false,                    // a procurar a cidade pelo GPS
    val isLoading: Boolean = false,                     // a criar a conta no Supabase
    @param:StringRes val message: Int? = null,          // aviso por baixo dos campos (ex.: falta o nome)
    val messageIsError: Boolean = true,                 // false: é uma boa notícia (ex.: confirma o email)
    val step: Int = 1,
    val totalSteps: Int = 3,
)
