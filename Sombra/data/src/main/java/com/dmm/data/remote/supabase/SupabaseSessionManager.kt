package com.dmm.data.remote.supabase

import com.dmm.data.local.AuthStore
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

// A sessão guardada no telemóvel (tokens do Supabase), para não ter de entrar sempre que abre a app
@Serializable
data class StoredSession(
    val accessToken: String,
    val refreshToken: String,
    val expiresAtMillis: Long,
    val userId: String,
    val email: String,
)

@Singleton
class SupabaseSessionManager @Inject constructor(
    private val store: AuthStore,
    private val api: SupabaseApi,
    private val clock: Clock,
) {
    private val _session = MutableStateFlow(store.read(KEY, StoredSession.serializer()))
    val session: StateFlow<StoredSession?> = _session.asStateFlow()

    // Dois pedidos ao mesmo tempo não podem renovar o token os dois
    private val refreshLock = Mutex()

    fun save(dto: SessionDto) {
        val session = StoredSession(
            accessToken = dto.accessToken,
            refreshToken = dto.refreshToken,
            expiresAtMillis = clock.millis() + dto.expiresIn * 1_000,
            userId = dto.user.id,
            email = dto.user.email.orEmpty(),
        )
        _session.value = session
        store.write(KEY, StoredSession.serializer(), session)
    }

    fun clear() {
        _session.value = null
        store.write(KEY, StoredSession.serializer(), null)
    }

    // Um token ainda válido (renova-o se estiver quase a expirar). null se não há sessão
    // ou se o Supabase recusou a renovação (aí a sessão acabou e é preciso entrar outra vez).
    suspend fun validSession(): StoredSession? = refreshLock.withLock {
        val current = _session.value ?: return null
        if (current.expiresAtMillis - clock.millis() > REFRESH_MARGIN_MILLIS) return current
        try {
            save(api.refreshSession(current.refreshToken))
            _session.value
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            clear()                     // refresh token inválido ou revogado
            null
        } catch (e: Exception) {
            current                     // sem rede: fica com a sessão, tenta-se mais tarde
        }
    }

    private companion object {
        const val KEY = "supabase_session"
        const val REFRESH_MARGIN_MILLIS = 60_000L
    }
}
