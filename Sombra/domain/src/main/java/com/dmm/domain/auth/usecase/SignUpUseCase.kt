package com.dmm.domain.auth.usecase

import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.model.SignUpOutcome
import com.dmm.domain.auth.repository.AuthRepository
import com.dmm.domain.profile.repository.UserRepository
import javax.inject.Inject

// Criar conta: regista no Supabase e, se já ficou com sessão, guarda o perfil no telemóvel
class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        city: String,
    ): AuthResult<SignUpOutcome> {
        val result = authRepository.signUp(email.trim(), password, name.trim(), city.trim())
        if (result is AuthResult.Success && result.data is SignUpOutcome.SignedIn) {
            userRepository.update {
                it.copy(name = name.trim(), email = email.trim(), city = city.trim(), onboardingCompleted = false)
            }
        }
        return result
    }
}
