package com.qinglong.panel.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.qinglong.panel.MainActivity
import com.qinglong.panel.R
import com.qinglong.panel.utils.LocalServerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class QingLongForegroundService : Service() {

    private val notificationId = 1001
    private val channelId = "qinglong_service_channel"
    private var localServerManager: LocalServerManager? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        localServerManager = LocalServerManager(this)
        createNotificationChannel()
        startForeground(notificationId, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startQingLongService()
        return START_STICKY
    }

    private fun startQingLongService() {
        serviceScope.launch {
            try {
                localServerManager?.startServer(5700) { success, message ->
                    if (success) {
                        updateNotification("青龙面板运行中", "端口: 5700")
                    } else {
                        updateNotification("服务启动失败", message)
                    }
                }
            } catch (e: Exception) {
                updateNotification("服务异常", e.message ?: "未知错误")
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "青龙面板服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "保持青龙面板在后台运行"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("青龙面板")
            .setContentText("正在运行...")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(title: String, text: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, createNotification())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        localServerManager?.stopServer()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}
