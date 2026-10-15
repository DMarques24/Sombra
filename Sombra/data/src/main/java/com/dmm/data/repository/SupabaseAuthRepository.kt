package com.dmm.data.repository

import com.dmm.data.remote.supabase.SessionDto
import com.dmm.data.remote.supabase.SupabaseApi
import com.dmm.data.remote.supabase.SupabaseSessionManager
import com.dmm.data.remote.supabase.safeAuthCall
import com.dmm.domain.auth.model.AuthResult
import com.dmm.domain.auth.model.AuthUser
import com.dmm.domain.auth.model.SignUpOutcome
import com.dmm.domain.auth.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SupabaseAuthRepository @Inject constructor(
    private val api: SupabaseApi,
    private val sessionManager: SupabaseSessionManager,
) : AuthRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeUser(): Flow<AuthUser?> =
        sessionManager.session.map { session -> session?.let { AuthUser(it.userId, it.email) } }

    override suspend fun signIn(email: String, password: String): AuthResult<AuthUser> = safeAuthCall {
        val session = api.signInWithPassword(email, password)
        sessionManager.save(session)
        session.toUser()
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        city: String,
    ): AuthResult<SignUpOutcome> = safeAuthCall {
        val response = api.signUp(email, password, name, city)
        // Sem confirmação de email, o registo devolve logo a sessão
        if ("access_token" in response) {
            val session = json.decodeFromJsonElement(SessionDto.serializer(), response)
            sessionManager.save(session)
            SignUpOutcome.SignedIn(session.toUser())
        } else {
            SignUpOutcome.ConfirmEmail
        }
    }

    override suspend fun sendPasswordReset(email: String): AuthResult<Unit> = safeAuthCall {
        api.recoverPassword(email)
    }

    override suspend fun signOut() {
        val session = sessionManager.session.value
        sessionManager.clear()          // sai já no telemóvel, mesmo sem rede
        if (session != null) {
            try {
                api.signOut(session.accessToken)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // o token expira sozinho: não vale a pena falhar por isto
            }
        }
    }

    override suspend fun deleteAccount(): AuthResult<Unit> = safeAuthCall {
        // Sem sessão válida não há conta para apagar: trata-se como sessão expirada
        val session = sessionManager.validSession() ?: throw IllegalStateException("Sem sessão")
        api.deleteAccount(session.accessToken)
    }

    private fun SessionDto.toUser() = AuthUser(user.id, user.email.orEmpty())
}
