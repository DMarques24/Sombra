package com.dmm.presentation.timer

import com.dmm.presentation.common.SPF_OPTIONS

// O ecrã do protetor tem três estados: sem temporizador -> a decorrer -> terminou.
// Os valores por defeito são os do design (usados nas previews).
sealed interface TimerUiState {

    // A ler o temporizador guardado e o UV de agora
    data object Loading : TimerUiState

    data class Idle(
        val uvNow: Int? = 8,                    // null: ainda sem tempo (ou sem rede)
        val recommendedSpf: Int = 50,
        val duration: String = "2:00",
        val spfOptions: List<Int> = SPF_OPTIONS,
        val selectedSpf: Int = 50,
        val goingToWater: Boolean = false,
    ) : TimerUiState

    data class Running(
        val spf: Int = 50,
        val appliedAt: String = "12:08",
        val remaining: String = "1:12:40",
        val progress: Float = 0.6f,             // fração do tempo que ainda falta (1f = acabou de aplicar)
        val notifyAt: String = "14:08",
        val uvNow: Int? = 8,
        val inWater: Boolean = false,
    ) : TimerUiState

    data class Finished(
        val appliedAt: String = "12:08",
        val elapsedMinutes: Int = 126,
        val finishedMinutesAgo: Int = 6,
        val uvNow: Int? = 7,
        val autoStopMinutes: Int = 24,
        val xpReward: Int = 20,
    ) : TimerUiState
}
