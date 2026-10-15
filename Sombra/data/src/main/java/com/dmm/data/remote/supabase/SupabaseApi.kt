package com.dmm.data.remote.supabase

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

// Os pedidos ao Supabase: Auth (/auth/v1) e a tabela de perfis (/rest/v1).
// Usa o mesmo HttpClient da Open-Meteo: sem SDK, só HTTP.
class SupabaseApi @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun signInWithPassword(email: String, password: String): SessionDto =
        client.post(auth("token")) {
            supabaseHeaders()
            parameter("grant_type", "password")
            setBody(PasswordGrantRequest(email, password))
        }.body()

    suspend fun refreshSession(refreshToken: String): SessionDto =
        client.post(auth("token")) {
            supabaseHeaders()
            parameter("grant_type", "refresh_token")
            setBody(RefreshGrantRequest(refreshToken))
        }.body()

    // Com a confirmação de email ligada vem só o utilizador; sem ela vem logo a sessão.
    // Por isso devolve o JSON cru e quem chama decide.
    suspend fun signUp(email: String, password: String, name: String, city: String): JsonObject =
        client.post(auth("signup")) {
            supabaseHeaders()
            setBody(SignUpRequest(email, password, SignUpMetadata(name, city)))
        }.body()

    suspend fun recoverPassword(email: String) {
        client.post(auth("recover")) {
            supabaseHeaders()
            setBody(RecoverRequest(email))
        }
    }

    suspend fun signOut(accessToken: String) {
        client.post(auth("logout")) {
            supabaseHeaders(accessToken)
        }
    }

    suspend fun getProfile(userId: String, accessToken: String): ProfileDto? =
        client.get(rest("profiles")) {
            supabaseHeaders(accessToken)
            parameter("id", "eq.$userId")
            parameter("select", "*")
        }.body<List<ProfileDto>>().firstOrNull()

    // Insere ou atualiza (a linha já é criada pelo trigger do registo, mas assim nunca falha)
    suspend fun upsertProfile(profile: ProfileDto, accessToken: String) {
        client.post(rest("profiles")) {
            supabaseHeaders(accessToken)
            header("Prefer", "resolution=merge-duplicates,return=minimal")
            setBody(profile)
        }
    }

    suspend fun getEvents(accessToken: String): List<SunscreenEventDto> =
        client.get(rest("sunscreen_events")) {
            supabaseHeaders(accessToken)
            parameter("select", "id,type,at,spf,uv_index,xp")
            parameter("order", "at")
        }.body()

    // Os ids vêm do telemóvel: enviar duas vezes o mesmo evento não o duplica
    suspend fun insertEvents(events: List<SunscreenEventDto>, accessToken: String) {
        if (events.isEmpty()) return
        client.post(rest("sunscreen_events")) {
            supabaseHeaders(accessToken)
            header("Prefer", "resolution=ignore-duplicates,return=minimal")
            setBody(events)
        }
    }

    // Edge Function que apaga a conta (só o servidor tem a chave para isso)
    suspend fun deleteAccount(accessToken: String) {
        client.post("${SupabaseConfig.url}/functions/v1/delete-account") {
            supabaseHeaders(accessToken)
        }
    }

    private fun auth(path: String) = "${SupabaseConfig.url}/auth/v1/$path"
    private fun rest(table: String) = "${SupabaseConfig.url}/rest/v1/$table"

    // A chave pública vai sempre no "apikey"; com sessão, o token da pessoa vai no Authorization
    // e o RLS só a deixa ver a sua linha. (As chaves sb_publishable_ não são JWT: nunca vão no Authorization.)
    private fun HttpRequestBuilder.supabaseHeaders(accessToken: String? = null) {
        header("apikey", SupabaseConfig.anonKey)
        if (accessToken != null) bearerAuth(accessToken)
        contentType(ContentType.Application.Json)
    }
}
