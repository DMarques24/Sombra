package com.dmm.domain.sunscreen.model

import java.time.Duration


sealed interface TimerStatus { // está a dizer ao compilador que todos os estados possíveis do temporizador são conhecidos em tempo de compilação e nenhum outro ficheiro ou módulo externo pode criar novos estados para esta interface.
    data object Idle : TimerStatus
    data class Running(val session: SunscreenSession, val remaining: Duration) : TimerStatus {
        val progress: Float
        get() = remaining.toMillis().toFloat() / session.duration.toMillis()
    }

    data class Finished(val session: SunscreenSession, val overdue: Duration) : TimerStatus
}