package com.dmm.domain.sunscreen.repository

import java.time.Instant

// Agenda a notificação "Hora de reaplicar". O :data decide como (AlarmManager)
interface ReminderScheduler {
    fun schedule(at: Instant)
    fun cancel()
}
