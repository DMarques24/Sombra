package com.dmm.domain.sunscreen.usecase

import com.dmm.domain.sunscreen.model.SunscreenEvent
import com.dmm.domain.sunscreen.repository.ReminderScheduler
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import kotlinx.coroutines.flow.first
import java.time.Clock
import javax.inject.Inject

// "Já saí do sol": pára o temporizador e cancela o aviso
class LeaveSunUseCase @Inject constructor(
    private val repository: SunscreenRepository,
    private val reminderScheduler: ReminderScheduler,
    private val clock: Clock,
) {
    suspend operator fun invoke(uvIndex: Int) {
        val session = repository.observeActiveSession().first() ?: return
        repository.stop()
        reminderScheduler.cancel()
        repository.record(SunscreenEvent(SunscreenEvent.Type.LeftSun, clock.instant(), session.spf, uvIndex, xp = 0))
    }
}
