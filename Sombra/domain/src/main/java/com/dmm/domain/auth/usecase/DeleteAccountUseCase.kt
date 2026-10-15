package com.dmm.domain.auth.usecase

import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.repository.AuthRepository
import javax.inject.Inject

// Apagar a conta: o Supabase apaga a conta, o perfil e o histórico; depois limpa-se o telemóvel.
// Se o pedido falhar (ex.: sem rede), nada é apagado e a pessoa pode tentar outra vez.
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val clearLocalData: ClearLocalDataUseCase,
) {
    suspend operator fun invoke(): AuthResult<Unit> {
        val result = authRepository.deleteAccount()
        if (result is AuthResult.Success) {
            authRepository.signOut()
            clearLocalData()
        }
        return result
    }
}
