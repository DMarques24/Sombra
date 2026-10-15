package com.dmm.data.remote.supabase

import com.dmm.domain.auth.model.AuthError
import org.junit.Assert.assertEquals
import org.junit.Test

// Respostas de erro reais do Supabase Auth (formato atual e formato antigo)
class SupabaseErrorsTest {

    @Test
    fun `credenciais erradas (formato atual, como respondeu o projeto Sombra)`() {
        val body = """{"code":400,"error_code":"invalid_credentials","msg":"Invalid login credentials"}"""
        assertEquals(AuthError.InvalidCredentials, authErrorFor(400, body))
    }

    @Test
    fun `credenciais erradas (formato antigo)`() {
        val body = """{"error":"invalid_grant","error_description":"Invalid login credentials"}"""
        assertEquals(AuthError.InvalidCredentials, authErrorFor(400, body))
    }

    @Test
    fun `email já registado`() {
        assertEquals(AuthError.EmailAlreadyUsed, authErrorFor(422, """{"error_code":"user_already_exists","msg":"User already registered"}"""))
        assertEquals(AuthError.EmailAlreadyUsed, authErrorFor(400, """{"msg":"User already registered"}"""))
    }

    @Test
    fun `palavra-passe fraca, email inválido e email por confirmar`() {
        assertEquals(AuthError.WeakPassword, authErrorFor(422, """{"error_code":"weak_password","msg":"Password should be at least 6 characters."}"""))
        assertEquals(AuthError.InvalidEmail, authErrorFor(400, """{"error_code":"email_address_invalid","msg":"Email address is invalid"}"""))
        assertEquals(AuthError.EmailNotConfirmed, authErrorFor(400, """{"error_code":"email_not_confirmed","msg":"Email not confirmed"}"""))
    }

    @Test
    fun `demasiados pedidos pelo estado HTTP ou pelo código`() {
        assertEquals(AuthError.TooManyRequests, authErrorFor(429, null))
        assertEquals(AuthError.TooManyRequests, authErrorFor(400, """{"error_code":"over_email_send_rate_limit"}"""))
    }

    @Test
    fun `corpo vazio ou que não é JSON dá Unknown`() {
        assertEquals(AuthError.Unknown, authErrorFor(500, null))
        assertEquals(AuthError.Unknown, authErrorFor(502, "<html>Bad gateway</html>"))
        assertEquals(AuthError.Unknown, authErrorFor(400, """{"error_code":"something_new"}"""))
    }
}
