package com.dmm.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Formatos do Supabase Auth (GoTrue) e da tabela "profiles" (PostgREST)

@Serializable
data class SessionDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    val user: SupabaseUserDto,
)

@Serializable
data class SupabaseUserDto(
    val id: String,
    val email: String? = null,
)

@Serializable
data class PasswordGrantRequest(val email: String, val password: String)

@Serializable
data class RefreshGrantRequest(@SerialName("refresh_token") val refreshToken: String)

@Serializable
data class SignUpRequest(val email: String, val password: String, val data: SignUpMetadata)

@Serializable
data class SignUpMetadata(val name: String, val city: String)

@Serializable
data class RecoverRequest(val email: String)

@Serializable
data class SunscreenEventDto(
    val id: String,
    val type: String,
    val at: String,                                 // timestamptz em ISO-8601
    val spf: Int,
    @SerialName("uv_index") val uvIndex: Int,
    val xp: Int,
)

@Serializable
data class ProfileDto(
    val id: String,
    val name: String = "",
    val email: String? = null,
    val city: String = "",
    @SerialName("skin_type") val skinType: String? = null,
    @SerialName("sun_places") val sunPlaces: List<String> = emptyList(),
    @SerialName("onboarding_completed") val onboardingCompleted: Boolean = false,
)
