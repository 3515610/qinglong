package com.qinglong.panel.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.qinglong.panel.MainActivity
import com.qinglong.panel.R
import com.qinglong.panel.TerminalActivity
import com.qinglong.panel.service.QingLongForegroundService

object NotificationHelper {

    const val ACTION_OPEN_PANEL = "com.qinglong.panel.ACTION_OPEN_PANEL"
    const val ACTION_OPEN_TERMINAL = "com.qinglong.panel.ACTION_OPEN_TERMINAL"
    const val ACTION_STOP_SERVICE = "com.qinglong.panel.ACTION_STOP_SERVICE"

    fun openPanel(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    fun openTerminal(context: Context) {
        val intent = Intent(context, TerminalActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    fun stopService(context: Context) {
        context.stopService(Intent(context, QingLongForegroundService::class.java))
    }
}
