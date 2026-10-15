package com.dmm.data.repository

import com.dmm.data.di.ApplicationScope
import com.dmm.data.local.LocalStore
import com.dmm.data.local.UserProfileEntity
import com.dmm.data.local.toDomain
import com.dmm.data.local.toEntity
import com.dmm.data.remote.supabase.ProfileDto
import com.dmm.data.remote.supabase.SupabaseApi
import com.dmm.data.remote.supabase.SupabaseSessionManager
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.profile.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// O telemóvel é a fonte do ecrã (funciona sem rede); o Supabase é a cópia que acompanha a conta.
// Cada alteração é guardada aqui logo e enviada para o Supabase em segundo plano.
@Singleton
class PreferencesUserRepository @Inject constructor(
    private val store: LocalStore,
    private val api: SupabaseApi,
    private val sessionManager: SupabaseSessionManager,
    @param:ApplicationScope private val appScope: CoroutineScope,
) : UserRepository {

    private val profile = MutableStateFlow(
        store.read(KEY, UserProfileEntity.serializer())?.toDomain() ?: UserProfile()
    )

    override fun observeProfile(): Flow<UserProfile> = profile.asStateFlow()

    override suspend fun update(transform: (UserProfile) -> UserProfile) {
        val updated = profile.updateAndGet(transform)
        saveLocally(updated)
        appScope.launch { pushToRemote(updated) }
    }

    override suspend fun syncFromRemote() {
        val session = sessionManager.validSession() ?: return
        val remote = try {
            api.getProfile(session.userId, session.accessToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return                      // sem rede: fica com o que está no telemóvel
        }
        val synced = remote?.toDomain(fallbackEmail = session.email) ?: UserProfile(email = session.email)
        profile.value = synced
        saveLocally(synced)
    }

    override suspend fun clear() {
        profile.value = UserProfile()
        store.write(KEY, UserProfileEntity.serializer(), null)
    }

    private fun saveLocally(value: UserProfile) = store.write(KEY, UserProfileEntity.serializer(), value.toEntity())

    // Sem sessão (ou sem rede) não envia; a próxima alteração com rede leva o perfil inteiro
    private suspend fun pushToRemote(value: UserProfile) {
        val session = sessionManager.validSession() ?: return
        try {
            api.upsertProfile(value.toDto(session.userId), session.accessToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // fica guardado no telemóvel de qualquer forma
        }
    }

    private fun UserProfile.toDto(userId: String) = toEntity().let {
        ProfileDto(
            id = userId,
            name = it.name,
            email = it.email.ifBlank { null },
            city = it.city,
            skinType = it.skinType,
            sunPlaces = it.sunPlaces,
            onboardingCompleted = it.onboardingCompleted,
        )
    }

    private fun ProfileDto.toDomain(fallbackEmail: String) = UserProfileEntity(
        name = name,
        email = email ?: fallbackEmail,
        city = city,
        skinType = skinType,
        sunPlaces = sunPlaces,
        onboardingCompleted = onboardingCompleted,
    ).toDomain()

    private companion object {
        const val KEY = "user_profile"
    }
}
