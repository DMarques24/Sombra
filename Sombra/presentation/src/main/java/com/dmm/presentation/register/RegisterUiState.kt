package com.dmm.presentation.register

enum class PasswordStrength { WEAK, MEDIUM, STRONG }

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val city: String = "",
    val passwordStrength: PasswordStrength? = null,     // null = ainda não há nada para avaliar
    val step: Int = 1,
    val totalSteps: Int = 3,
)
