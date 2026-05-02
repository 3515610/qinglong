package com.qinglong.panel.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.qinglong.panel.R
import com.qinglong.panel.utils.LocalServerManager
import timber.log.Timber

class QingLongWebServerService : LifecycleService() {

    private val notificationId = 1002
    private val channelId = "qinglong_webserver_channel"
    private lateinit var localServerManager: LocalServerManager

    override fun onCreate() {
        super.onCreate()
        localServerManager = LocalServerManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startWebServer()
        return START_STICKY
    }

    private fun startWebServer() {
        localServerManager.startServer(5700) { success, message ->
            if (success) {
                startForeground(notificationId, createNotification("运行中", "端口: 5700"))
            } else {
                Timber.e("Web server start failed: $message")
                stopSelf()
            }
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "青龙Web服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "青龙面板本地Web服务"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(status: String, detail: String): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("青龙面板服务")
            .setContentText("$status - $detail")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        localServerManager.stopServer()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}
