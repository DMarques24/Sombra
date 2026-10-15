package com.dmm.domain.sunscreen.model

import java.time.Instant

// Uma coisa que a pessoa fez com o protetor. O histórico destes eventos dá o XP, os níveis e os patches.
data class SunscreenEvent(
    val type: Type,
    val at: Instant,
    val spf: Int,
    val uvIndex: Int,               // o UV no momento, para os patches de UV alto
    val xp: Int,
) {
    enum class Type {
        Applied,                    // começou um temporizador
        ReappliedOnTime,            // reaplicou perto da hora (ou até 30 min depois)
        ReappliedEarly,             // reaplicou muito antes da hora: conta, mas dá pouco XP
        LeftSun,                    // "Já saí do sol"
    }

    val isApplication: Boolean get() = type != Type.LeftSun
}
