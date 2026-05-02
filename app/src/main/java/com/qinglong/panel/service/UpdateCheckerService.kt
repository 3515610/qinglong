package com.qinglong.panel.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.qinglong.panel.database.QingLongDatabase
import com.qinglong.panel.database.UpdateHistoryEntity
import com.qinglong.panel.utils.QingLongUpdater
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

            var hasUpdate = false

            Thread {
                val result = updater.checkForUpdates { updateResult ->
                    when (updateResult) {
                        is QingLongUpdater.UpdateResult.Success -> {
                            if (updateResult.updates.isNotEmpty()) {
                                hasUpdate = true
                                updateResult.updates.forEach { update ->
                                    val updateHistory = UpdateHistoryEntity(
                                        version = update.latestVersion,
                                        status = 1,
                                        description = update.description
                                    )
                                    
                                    Thread {
                                        database.updateHistoryDao().insertUpdate(updateHistory)
                                    }.start()
                                }
                            }
                        }
                        is QingLongUpdater.UpdateResult.Error -> {
                            Timber.e("Update check failed: ${updateResult.message}")
                        }
                    }
                }
            }.join()

            if (hasUpdate) {
                Result.success()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Timber.e(e, "Update check failed")
            Result.retry()
        }
    }
}
