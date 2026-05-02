package com.qinglong.panel.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.qinglong.panel.utils.NotificationHelper
import timber.log.Timber

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        when (action) {
            NotificationHelper.ACTION_OPEN_PANEL -> {
                Timber.d("Open panel action received")
                NotificationHelper.openPanel(context)
            }
            NotificationHelper.ACTION_OPEN_TERMINAL -> {
                Timber.d("Open terminal action received")
                NotificationHelper.openTerminal(context)
            }
            NotificationHelper.ACTION_STOP_SERVICE -> {
                Timber.d("Stop service action received")
                NotificationHelper.stopService(context)
            }
        }
    }
}
