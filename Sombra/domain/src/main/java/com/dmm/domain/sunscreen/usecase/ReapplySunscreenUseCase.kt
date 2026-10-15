package com.dmm.domain.sunscreen.usecase

import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.model.SunscreenRules
import com.dmm.domain.sunscreen.repository.ReminderScheduler
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Duration
import javax.inject.Inject

// "Reapliquei": recomeça a contagem com o mesmo SPF. Dá mais XP se for perto da hora certa.
class ReapplySunscreenUseCase @Inject constructor(
    private val repository: SunscreenRepository,
    private val reminderScheduler: ReminderScheduler,
    private val clock: Clock,
) {
    // Devolve o XP ganho
    suspend operator fun invoke(uvIndex: Int): Int {
        val previous = repository.observeActiveSession().first()
        val now = clock.instant()
        val onTime = previous != null &&
            !Duration.between(now, previous.endsAt).minus(SunscreenRules.ON_TIME_WINDOW).isPositive

        val session = repository.reapply() ?: return 0
        val type = if (onTime) SunscreenEvent.Type.ReappliedOnTime else SunscreenEvent.Type.ReappliedEarly
        val xp = if (onTime) SunscreenRules.XP_REAPPLY_ON_TIME else SunscreenRules.XP_REAPPLY_EARLY
        repository.record(SunscreenEvent(type, now, session.spf, uvIndex, xp))
        reminderScheduler.schedule(session.endsAt)
        return xp
    }
}
