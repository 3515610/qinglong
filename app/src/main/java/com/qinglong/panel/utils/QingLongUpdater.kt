package com.qinglong.panel.utils

import android.content.Context
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class QingLongUpdater(private val context: Context) {

    companion object {
        private const val QINGLONG_GITHUB_API = "https://api.github.com/repos/whyour/qinglong/releases/latest"
    }

    sealed class UpdateResult {
        data class Success(val updates: List<UpdateInfo>) : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }

    data class UpdateInfo(
        val name: String,
        val currentVersion: String,
        val latestVersion: String,
        val description: String,
        val downloadUrl: String,
        val size: Long,
        var isChecked: Boolean = false
    )

    fun checkForUpdates(callback: (UpdateResult) -> Unit) {
        Thread {
            try {
                val updates = checkQingLongUpdate()
                callback(UpdateResult.Success(updates))
            } catch (e: Exception) {
                Timber.e(e, "Update check failed")
                callback(UpdateResult.Error(e.message ?: "未知错误"))
            }
        }.start()
    }

    fun checkForUpdatesSync(): List<UpdateInfo> {
        return try {
            checkQingLongUpdate()
        } catch (e: Exception) {
            Timber.e(e, "Update check failed")
            emptyList()
        }
    }

    private fun checkQingLongUpdate(): List<UpdateInfo> {
        val updates = mutableListOf<UpdateInfo>()

        try {
            val url = URL(QINGLONG_GITHUB_API)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use {
                    it.readText()
                }

                val json = JSONObject(response)
                val latestVersion = json.getString("tag_name")
                val description = json.optString("body", "无更新说明")
                val downloadUrl = json.optJSONArray("assets")?.let { assets ->
                    if (assets.length() > 0) {
                        assets.getJSONObject(0).getString("browser_download_url")
                    } else {
                        ""
                    }
                } ?: ""

                val currentVersion = getCurrentVersion()
                if (latestVersion != currentVersion) {
                    updates.add(
                        UpdateInfo(
                            name = "青龙面板",
                            currentVersion = currentVersion,
                            latestVersion = latestVersion,
                            description = description,
                            downloadUrl = downloadUrl,
                            size = 0
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to check for updates")
        }

        return updates
    }

    private fun getCurrentVersion(): String {
        return try {
            val qinglongDir = File(context.filesDir, "qinglong")
            val versionFile = File(qinglongDir, "version")
            if (versionFile.exists()) {
                versionFile.readText().trim()
            } else {
                "unknown"
            }
        } catch (e: Exception) {
            "unknown"
        }
    }

    fun updateAll(callback: (Boolean, String) -> Unit) {
        Thread {
            try {
                updateQingLong { success, message ->
                    if (success) {
                        updateNodeModules { nodeSuccess, nodeMessage ->
                            callback(nodeSuccess, nodeMessage)
                        }
                    } else {
                        callback(false, message)
                    }
                }
            } catch (e: Exception) {
                callback(false, e.message ?: "更新失败")
            }
        }.start()
    }

    private fun updateQingLong(callback: (Boolean, String) -> Unit) {
        try {
            val qinglongDir = File(context.filesDir, "qinglong")

            val processBuilder = ProcessBuilder(
                "sh", "-c",
                "cd ${qinglongDir.absolutePath} && git fetch origin && git reset --hard origin/master"
            )
            processBuilder.directory(qinglongDir)
            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                updateVersionFile()
                callback(true, "更新成功")
            } else {
                val errorOutput = BufferedReader(InputStreamReader(process.inputStream)).use {
                    it.readText()
                }
                callback(false, "更新失败：$errorOutput")
            }
        } catch (e: Exception) {
            callback(false, e.message ?: "更新异常")
        }
    }

    private fun updateNodeModules(callback: (Boolean, String) -> Unit) {
        try {
            val qinglongDir = File(context.filesDir, "qinglong")
            val nodeBinDir = File(context.filesDir, "nodejs/bin")

            val processBuilder = ProcessBuilder(
                "sh", "-c",
                "export PATH=${nodeBinDir.absolutePath}:\$PATH && cd ${qinglongDir.absolutePath} && npm install --production"
            )
            processBuilder.directory(qinglongDir)
            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                callback(true, "依赖更新成功")
            } else {
                val errorOutput = BufferedReader(InputStreamReader(process.inputStream)).use {
                    it.readText()
                }
                callback(false, "依赖更新失败：$errorOutput")
            }
        } catch (e: Exception) {
            callback(false, e.message ?: "依赖更新异常")
        }
    }

    private fun updateVersionFile() {
        try {
            val versionFile = File(context.filesDir, "qinglong/version")
            val latestVersion = getLatestVersionFromAPI()
            if (latestVersion.isNotEmpty()) {
                versionFile.writeText(latestVersion)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update version file")
        }
    }

    private fun getLatestVersionFromAPI(): String {
        return try {
            val url = URL(QINGLONG_GITHUB_API)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use {
                    it.readText()
                }
                JSONObject(response).getString("tag_name")
            } else {
                ""
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get latest version")
            ""
        }
    }
}
