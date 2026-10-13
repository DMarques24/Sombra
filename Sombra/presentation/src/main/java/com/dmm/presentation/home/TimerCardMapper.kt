package com.dmm.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.dmm.domain.sunscreen.model.TimerStatus
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@RequiresApi(Build.VERSION_CODES.O)
internal fun TimerStatus.toCard(uvNow: Int): TimerCardUiState = when (this) {
    TimerStatus.Idle -> TimerCardUiState.Idle
    is TimerStatus.Running -> TimerCardUiState.Running(
        remainingHours = remaining.toHours().toInt(),
        remainingMinutes = (remaining.toMinutes() % 60).toInt(),
        spf = session.spf,
        appliedAt = session.appliedAt.atZone(ZoneId.systemDefault()).format(TIME_FORMAT),
        progress = progress,
    )
    is TimerStatus.Finished -> TimerCardUiState.Finished(
        overdueMinutes = overdue.toMinutes().toInt(),
        uvNow = uvNow,
    )
}
