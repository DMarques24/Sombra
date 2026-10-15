package com.dmm.data.remote.supabase

import com.dmm.domain.auth.model.AuthError
import com.dmm.domain.auth.model.AuthResult
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException

private val json = Json { ignoreUnknownKeys = true }

// Como o safeApiCall, mas para o Auth: traduz as respostas de erro do Supabase em AuthError
internal suspend fun <T> safeAuthCall(block: suspend () -> T): AuthResult<T> {
    if (!SupabaseConfig.isConfigured) return AuthResult.Failure(AuthError.NotConfigured)
    return try {
        AuthResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        AuthResult.Failure(AuthError.NoConnection)
    } catch (e: ResponseException) {
        AuthResult.Failure(e.toAuthError())
    } catch (e: Exception) {
        AuthResult.Failure(AuthError.Unknown)
    }
}

private suspend fun ResponseException.toAuthError(): AuthError =
    authErrorFor(response.status.value, runCatching { response.bodyAsText() }.getOrNull())

// O Supabase manda {"error_code": "...", "msg": "..."} (ou, nas versões antigas, {"error": "...", "error_description": "..."}).
// Função pura (estado HTTP + corpo -> erro) para ser testada sem rede.
internal fun authErrorFor(status: Int, body: String?): AuthError {
    if (status == 429) return AuthError.TooManyRequests
    val json = body?.let { runCatching { json.decodeFromString<JsonObject>(it) }.getOrNull() }
    val code = listOf("error_code", "error", "code")
        .firstNotNullOfOrNull { key -> json?.get(key)?.let { runCatching { it.jsonPrimitive.content }.getOrNull() } }
        .orEmpty()
    val message = listOf("msg", "message", "error_description")
        .firstNotNullOfOrNull { key -> json?.get(key)?.let { runCatching { it.jsonPrimitive.content }.getOrNull() } }
        .orEmpty()
        .lowercase()

    return when {
        code == "invalid_credentials" || code == "invalid_grant" || "invalid login" in message ->
            AuthError.InvalidCredentials
        code == "user_already_exists" || code == "email_exists" || "already registered" in message ->
            AuthError.EmailAlreadyUsed
        code == "weak_password" || "password should" in message -> AuthError.WeakPassword
        code == "email_not_confirmed" || "not confirmed" in message -> AuthError.EmailNotConfirmed
        code == "email_address_invalid" || code == "validation_failed" || "invalid format" in message ->
            AuthError.InvalidEmail
        code.startsWith("over_") -> AuthError.TooManyRequests
        else -> AuthError.Unknown
    }
}
