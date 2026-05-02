package com.qinglong.panel.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.qinglong.panel.database.QingLongDatabase
import com.qinglong.panel.database.UpdateHistoryEntity
import com.qinglong.panel.utils.QingLongUpdater
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class UpdateCheckerService(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {
            Timber.d("Checking for updates...")
            
            val updater = QingLongUpdater(applicationContext)
            val database = QingLongDatabase.getInstance(applicationContext)

            runBlocking {
                updater.checkForUpdatesSync().forEach { update ->
                    val updateHistory = UpdateHistoryEntity(
                        version = update.latestVersion,
                        status = 1,
                        description = update.description
                    )
                    database.updateHistoryDao().insertUpdate(updateHistory)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Update check failed")
            Result.success()
        }
    }
}
