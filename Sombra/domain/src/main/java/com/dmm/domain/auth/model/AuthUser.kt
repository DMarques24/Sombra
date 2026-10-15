package com.dmm.domain.auth.model

// A pessoa com sessão iniciada no Supabase
data class AuthUser(
    val id: String,
    val email: String,
)
