package com.dmm.domain.auth.usecase

import com.dmm.domain.auth.repository.AuthRepository
import javax.inject.Inject

// Terminar sessão: sai do Supabase e limpa os dados da conta do telemóvel
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val clearLocalData: ClearLocalDataUseCase,
) {
    suspend operator fun invoke() {
        authRepository.signOut()
        clearLocalData()
    }
}
