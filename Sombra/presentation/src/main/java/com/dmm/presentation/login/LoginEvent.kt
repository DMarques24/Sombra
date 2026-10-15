package com.dmm.presentation.login

sealed interface LoginEvent {
    data class SignedIn(val onboardingCompleted: Boolean) : LoginEvent
}
