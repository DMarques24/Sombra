package com.dmm.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dmm.domain.sunscreen.repository.ReminderScheduler
import com.dmm.domain.sunscreen.repository.SunscreenRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Inject

// O Android apaga os alarmes quando o telemóvel reinicia: volta a agendar o aviso do protetor
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var sunscreenRepository: SunscreenRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var clock: Clock

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        // goAsync: o Android dá uns segundos para acabar o trabalho fora da thread principal
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val session = sunscreenRepository.observeActiveSession().first()
                if (session != null && session.endsAt.isAfter(clock.instant())) {
                    reminderScheduler.schedule(session.endsAt)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
