package com.dmm.domain.weather.usecase

import com.dmm.domain.profile.model.SkinType
import com.dmm.domain.weather.model.HourRange
import com.dmm.domain.weather.model.HourlyUv
import com.dmm.domain.weather.model.SunRisk
import javax.inject.Inject
import kotlin.math.roundToInt

// Contas feitas só com o tipo de pele e o UV: não precisa de repositórios
class CalculateSunRiskUseCase @Inject constructor() {

    operator fun invoke(skinType: SkinType, uvNow: Double, hourlyToday: List<HourlyUv>): SunRisk {
        val maxUv = maxOf(uvNow, hourlyToday.maxOfOrNull { it.uvIndex } ?: 0.0)
        return SunRisk(
            burnMinutes = burnMinutes(skinType, uvNow),
            shadeHours = hoursWith(hourlyToday) { it >= SHADE_UV },
            protectHours = hoursWith(hourlyToday) { it >= PROTECT_UV },
            recommendedSpf = recommendedSpf(skinType, maxUv),
        )
    }

    // Cada ponto de UV dá 0,025 W/m² de UV "que queima" = 1,5 J/m² por minuto.
    // Minutos até queimar = dose que a pele aguenta (MED) / dose por minuto.
    private fun burnMinutes(skinType: SkinType, uv: Double): Int? {
        if (uv < 1.0) return null
        return (skinType.med / (uv * JOULES_PER_UV_MINUTE)).roundToInt()
    }

    // Da primeira à última hora que cumpre a condição (a última conta inteira: até à hora seguinte)
    private fun hoursWith(hourly: List<HourlyUv>, condition: (Double) -> Boolean): HourRange? {
        val hours = hourly.filter { condition(it.uvIndex) }.map { it.time.hour }
        if (hours.isEmpty()) return null
        return HourRange(startHour = hours.min(), endHour = hours.max() + 1)
    }

    private fun recommendedSpf(skinType: SkinType, maxUv: Double): Int = when (skinType) {
        SkinType.I, SkinType.II -> if (maxUv < PROTECT_UV) 30 else 50
        SkinType.III -> if (maxUv >= SHADE_UV) 50 else 30
        SkinType.IV -> if (maxUv >= 8) 50 else 30
        SkinType.V, SkinType.VI -> if (maxUv >= 8) 30 else 15
    }

    private companion object {
        const val JOULES_PER_UV_MINUTE = 1.5
        const val PROTECT_UV = 3.0
        const val SHADE_UV = 6.0
    }
}
