package com.qinglong.panel

import android.app.Application
import android.os.Build
import androidx.work.*
import com.qinglong.panel.database.QingLongDatabase
import com.qinglong.panel.service.UpdateCheckerService
import timber.log.Timber
import java.util.concurrent.TimeUnit

class QingLongApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        initializeDatabase()
        scheduleUpdateChecker()
    }

    private fun initializeDatabase() {
        QingLongDatabase.getInstance(this)
    }

    private fun scheduleUpdateChecker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<UpdateCheckerWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag("update_checker")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "update_checker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    companion object {
        lateinit var instance: QingLongApplication
            private set
    }
}
