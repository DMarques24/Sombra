package com.dmm.domain.sunscreen.usecase

import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.model.SunscreenRules
import com.dmm.domain.sunscreen.repository.ReminderScheduler
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import java.time.Clock
import javax.inject.Inject

// "Apliquei protetor agora": começa o temporizador, guarda no histórico e agenda o aviso
class ApplySunscreenUseCase @Inject constructor(
    private val repository: SunscreenRepository,
    private val reminderScheduler: ReminderScheduler,
    private val clock: Clock,
) {
    suspend operator fun invoke(spf: Int, inWater: Boolean, uvIndex: Int) {
        val session = repository.start(spf, inWater)
        repository.record(
            SunscreenEvent(
                type = SunscreenEvent.Type.Applied,
                at = clock.instant(),
                spf = spf,
                uvIndex = uvIndex,
                xp = SunscreenRules.XP_APPLY,
            )
        )
        reminderScheduler.schedule(session.endsAt)
    }
}
