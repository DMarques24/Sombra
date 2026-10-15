package com.dmm.domain.auth.usecase

import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.model.AuthUser
import com.dmm.domain.auth.repository.AuthRepository
import com.dmm.domain.profile.repository.UserRepository
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import javax.inject.Inject

// Entrar: inicia sessão e traz o perfil e o histórico guardados no Supabase
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sunscreenRepository: SunscreenRepository,
) {
    suspend operator fun invoke(email: String, password: String): AuthResult<AuthUser> {
        val result = authRepository.signIn(email.trim(), password)
        if (result is AuthResult.Success) {
            userRepository.syncFromRemote()
            sunscreenRepository.syncFromRemote()
        }
        return result
    }
}
