package com.dmm.domain.auth.repository

import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.model.AuthUser
import com.dmm.domain.auth.model.SignUpOutcome
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // null quando não há sessão iniciada
    fun observeUser(): Flow<AuthUser?>

    suspend fun signIn(email: String, password: String): AuthResult<AuthUser>

    // name e city vão nos metadados: o Supabase cria a linha do perfil logo no registo
    suspend fun signUp(email: String, password: String, name: String, city: String): AuthResult<SignUpOutcome>

    suspend fun sendPasswordReset(email: String): AuthResult<Unit>

    suspend fun signOut()

    // Apaga a conta e todos os dados dela no Supabase (obrigatório no Google Play)
    suspend fun deleteAccount(): AuthResult<Unit>
}
