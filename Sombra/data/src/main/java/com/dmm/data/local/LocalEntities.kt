package com.dmm.data.local

import com.dmm.data.remote.dto.ForecastResponseDto
import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.profile.model.SunPlace
import com.dmm.domain.profile.model.UserProfile
import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.model.SunscreenSession
import kotlinx.serialization.Serializable
import com.dmm.data.remote.supabase.SunscreenEventDto
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID

// O formato em que as coisas ficam guardadas no telemóvel. Os enums vão pelo nome
// e as datas em milissegundos, para o JSON não depender do java.time.

@Serializable
data class UserProfileEntity(
    val name: String = "",
    val email: String = "",
    val city: String = "",
    val skinType: String? = null,
    val sunPlaces: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false,
)

fun UserProfileEntity.toDomain() = UserProfile(
    name = name,
    email = email,
    city = city,
    skinType = skinType?.let { name -> SkinType.entries.firstOrNull { it.name == name } },
    sunPlaces = sunPlaces.mapNotNull { name -> SunPlace.entries.firstOrNull { it.name == name } }.toSet(),
    onboardingCompleted = onboardingCompleted,
)

fun UserProfile.toEntity() = UserProfileEntity(
    name = name,
    email = email,
    city = city,
    skinType = skinType?.name,
    sunPlaces = sunPlaces.map { it.name },
    onboardingCompleted = onboardingCompleted,
)

@Serializable
data class SunscreenSessionEntity(val appliedAtMillis: Long, val spf: Int, val inWater: Boolean)

fun SunscreenSessionEntity.toDomain() = SunscreenSession(Instant.ofEpochMilli(appliedAtMillis), spf, inWater)

fun SunscreenSession.toEntity() = SunscreenSessionEntity(appliedAt.toEpochMilli(), spf, inWater)

// id: gerado no telemóvel, é o mesmo no Supabase (assim sincronizar nunca duplica).
// Os eventos guardados antes de haver ids recebem um ao serem lidos.
@Serializable
data class SunscreenEventEntity(
    val type: String,
    val atMillis: Long,
    val spf: Int,
    val uvIndex: Int,
    val xp: Int,
    val id: String = UUID.randomUUID().toString(),
)

fun SunscreenEventEntity.toDomain(): SunscreenEvent? {
    val type = SunscreenEvent.Type.entries.firstOrNull { it.name == type } ?: return null
    return SunscreenEvent(type, Instant.ofEpochMilli(atMillis), spf, uvIndex, xp)
}

fun SunscreenEvent.toEntity() = SunscreenEventEntity(type.name, at.toEpochMilli(), spf, uvIndex, xp)

fun SunscreenEventEntity.toDto() = SunscreenEventDto(id, type, Instant.ofEpochMilli(atMillis).toString(), spf, uvIndex, xp)

// O Supabase devolve "2026-10-15T19:42:00+00:00": OffsetDateTime lê os dois formatos
fun SunscreenEventDto.toEntity() =
    SunscreenEventEntity(type, OffsetDateTime.parse(at).toInstant().toEpochMilli(), spf, uvIndex, xp, id)

// A última resposta da Open-Meteo, para mostrar quando não há internet
@Serializable
data class CachedForecast(
    val latitude: Double,
    val longitude: Double,
    val fetchedAtMillis: Long,
    val response: ForecastResponseDto,
)
