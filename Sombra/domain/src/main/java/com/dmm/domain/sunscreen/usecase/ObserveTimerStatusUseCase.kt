package com.dmm.domain.sunscreen.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.dmm.domain.sunscreen.model.TimerStatus
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class ObserveTimerStatusUseCase @RequiresApi(Build.VERSION_CODES.O)
@Inject constructor(
    private val repository: SunscreenRepository,
    private val clock: Clock,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(): Flow<TimerStatus> =
        combine(repository.observeActiveSession(), everySecond()) { session, _ ->
            if (session == null) return@combine TimerStatus.Idle
            val now = clock.instant()
            val remaining = Duration.between(now, session.endsAt)
            when {
                !remaining.isNegative -> TimerStatus.Running(session, remaining)
                remaining.abs() < AUTO_STOP -> TimerStatus.Finished(session, remaining.abs())
                else -> TimerStatus.Idle
            }
        }

    private fun everySecond() = flow {
        while (true) {
            emit(Unit); delay(1.seconds)
        }
    }

    private companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val AUTO_STOP: Duration = Duration.ofMinutes(30)
    }
}