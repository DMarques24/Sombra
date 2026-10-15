package com.dmm.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// Acordado pelo AlarmManager quando chega a hora de reaplicar
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ReminderNotifications.show(context)
    }
}
