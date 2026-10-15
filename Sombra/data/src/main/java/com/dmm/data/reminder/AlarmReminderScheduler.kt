package com.dmm.data.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.dmm.domain.sunscreen.repository.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject

// Agenda o aviso com o AlarmManager: toca mesmo com a app fechada.
// É "inexato" (pode chegar uns minutos depois) para não precisar da permissão de alarmes exatos.
class AlarmReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(at: Instant) {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at.toEpochMilli(), pendingIntent())
        // Reaplicou: o aviso anterior (se ainda estiver na barra) já não faz sentido
        ReminderNotifications.dismiss(context)
    }

    override fun cancel() {
        alarmManager.cancel(pendingIntent())
        ReminderNotifications.dismiss(context)
    }

    // Sempre o mesmo PendingIntent: agendar outra vez substitui o aviso anterior
    private fun pendingIntent(): PendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        Intent(context, ReminderReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private companion object {
        const val REQUEST_CODE = 1
    }
}
