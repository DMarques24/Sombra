package com.dmm.domain.sunscreen.model

import java.time.Duration

// As regras do temporizador num só sítio, para o ecrã e os use cases dizerem o mesmo
object SunscreenRules {
    // Sem resposta passado este tempo do fim, o temporizador desliga-se sozinho
    val AUTO_STOP: Duration = Duration.ofMinutes(30)

    // Reaplicar a menos disto do fim já conta como "a tempo"
    val ON_TIME_WINDOW: Duration = Duration.ofMinutes(30)

    const val XP_APPLY = 10
    const val XP_REAPPLY_ON_TIME = 20
    const val XP_REAPPLY_EARLY = 5
}
