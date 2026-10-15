package com.dmm.data.repository

import com.dmm.data.di.ApplicationScope
import com.dmm.data.local.LocalStore
import com.dmm.data.local.SunscreenEventEntity
import com.dmm.data.local.SunscreenSessionEntity
import com.dmm.data.local.toDomain
import com.dmm.data.local.toDto
import com.dmm.data.local.toEntity
import com.dmm.data.remote.supabase.SupabaseApi
import com.dmm.data.remote.supabase.SupabaseSessionManager
import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.model.SunscreenSession
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

// Uma só instância na app, partilhada pelo Início, pelo Protetor e pelo Perfil.
// O temporizador fica só no telemóvel; o histórico (que dá o XP) também vai para o Supabase.
@Singleton
class PreferencesSunscreenRepository @Inject constructor(
    private val store: LocalStore,
    private val api: SupabaseApi,
    private val sessionManager: SupabaseSessionManager,
    @param:ApplicationScope private val appScope: CoroutineScope,
    private val clock: Clock,
) : SunscreenRepository {

    private val session = MutableStateFlow(
        store.read(KEY_SESSION, SunscreenSessionEntity.serializer())?.toDomain()
    )

    // Guardam-se as entidades (com o id) para a sincronização; o domínio só vê os eventos
    private val history = MutableStateFlow(
        store.read(KEY_HISTORY, HistorySerializer).orEmpty().also { saveHistory(it) }
    )

    override fun observeActiveSession(): Flow<SunscreenSession?> = session.asStateFlow()

    override suspend fun start(spf: Int, inWater: Boolean): SunscreenSession {
        val started = SunscreenSession(clock.instant(), spf, inWater)
        saveSession(started)
        return started
    }

    override suspend fun reapply(): SunscreenSession? {
        val reapplied = session.value?.copy(appliedAt = clock.instant()) ?: return null
        saveSession(reapplied)
        return reapplied
    }

    override suspend fun stop() = saveSession(null)

    override fun observeHistory(): Flow<List<SunscreenEvent>> =
        history.map { entities -> entities.mapNotNull { it.toDomain() } }

    override suspend fun record(event: SunscreenEvent) {
        val entity = event.toEntity()
        saveHistory(history.updateAndGet { it + entity })
        // Em segundo plano: sem rede fica só aqui e vai na próxima sincronização
        appScope.launch { push(listOf(entity)) }
    }

    override suspend fun syncFromRemote() {
        val auth = sessionManager.validSession() ?: return
        try {
            // Primeiro envia o que só existe aqui (ex.: eventos feitos sem rede), depois traz tudo
            api.insertEvents(history.value.map { it.toDto() }, auth.accessToken)
            val remote = api.getEvents(auth.accessToken).map { it.toEntity() }
            // Junta (em vez de substituir) para não perder um evento registado durante a sincronização
            val merged = history.updateAndGet { local ->
                (remote + local).distinctBy { it.id }.sortedBy { it.atMillis }
            }
            saveHistory(merged)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // sem rede: fica com o histórico do telemóvel
        }
    }

    override suspend fun clear() {
        saveSession(null)
        history.value = emptyList()
        saveHistory(emptyList())
    }

    private suspend fun push(events: List<SunscreenEventEntity>) {
        val auth = sessionManager.validSession() ?: return
        try {
            api.insertEvents(events.map { it.toDto() }, auth.accessToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // fica no telemóvel; a sincronização ao entrar/abrir a app envia-o
        }
    }

    private fun saveSession(value: SunscreenSession?) {
        session.value = value
        store.write(KEY_SESSION, SunscreenSessionEntity.serializer(), value?.toEntity())
    }

    private fun saveHistory(entities: List<SunscreenEventEntity>) =
        store.write(KEY_HISTORY, HistorySerializer, entities)

    private companion object {
        const val KEY_SESSION = "sunscreen_session"
        const val KEY_HISTORY = "sunscreen_history"
        val HistorySerializer = ListSerializer(SunscreenEventEntity.serializer())
    }
}
