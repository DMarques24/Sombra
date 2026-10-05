package com.dmm.presentation.timer

// O ecrã do protetor tem três estados, um por cada mock:
// sem temporizador -> a decorrer -> terminou.
// Os valores por defeito são os do design, até haver lógica a sério.
sealed interface TimerUiState {

    data class Idle(
        val uvNow: Int = 8,
        val recommendedSpf: Int = 50,
        val duration: String = "2:00",
        val spfOptions: List<String> = listOf("15", "30", "50", "50+"),
        val selectedSpf: String = "50",
        val goingToWater: Boolean = false,
    ) : TimerUiState

    data class Running(
        val spf: Int = 50,
        val appliedAt: String = "12:08",
        val remaining: String = "1:12:40",
        val progress: Float = 0.6f,          // fração do tempo que ainda falta (1f = acabou de aplicar)
        val notifyAt: String = "14:08",
        val uvNow: Int = 8,
        val inWater: Boolean = false,
    ) : TimerUiState

    data class Finished(
        val appliedAt: String = "12:08",
        val elapsedHours: Int = 2,
        val finishedMinutesAgo: Int = 6,
        val uvNow: Int = 7,
        val autoStopMinutes: Int = 30,
        val xpReward: Int = 20,
    ) : TimerUiState
}
