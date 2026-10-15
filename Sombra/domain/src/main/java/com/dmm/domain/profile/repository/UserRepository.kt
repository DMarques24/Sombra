package com.dmm.domain.profile.repository

import com.dmm.domain.profile.model.UserProfile
import kotlinx.coroutines.flow.Flow

// O perfil fica guardado no telemóvel (funciona sem rede) e é copiado para o Supabase quando há sessão
interface UserRepository {
    fun observeProfile(): Flow<UserProfile>
    suspend fun update(transform: (UserProfile) -> UserProfile)

    // Traz o perfil do Supabase para o telemóvel (depois de entrar)
    suspend fun syncFromRemote()

    // Apaga o perfil do telemóvel (ao terminar sessão)
    suspend fun clear()
}
