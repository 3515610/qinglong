package com.qinglong.panel.utils

import android.content.Context
import timber.log.Timber
import java.io.File

object EnvironmentChecker {

    private const val PROOT_MARKER = "proot_installed"
    private const val NODEJS_MARKER = "nodejs_installed"
    private const val QINGLONG_MARKER = "qinglong_installed"

    fun isPRootInstalled(context: Context): Boolean {
        val markerFile = File(context.filesDir, PROOT_MARKER)
        return markerFile.exists() && isPRootExecutableAvailable()
    }

    fun isNodeJSInstalled(context: Context): Boolean {
        val markerFile = File(context.filesDir, NODEJS_MARKER)
        return markerFile.exists() && isNodeJSAvailable()
    }

    fun isQingLongInstalled(context: Context): Boolean {
        val markerFile = File(context.filesDir, QINGLONG_MARKER)
        val qinglongDir = File(context.filesDir, "qinglong")
        return markerFile.exists() && qinglongDir.exists()
    }

    private fun isPRootExecutableAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("proot", "--version"))
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun isNodeJSAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("node", "--version"))
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    fun installPRoot(context: Context, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val success = installPRootSync(context)
                callback(success)
            } catch (e: Exception) {
                Timber.e(e, "Failed to install PRoot")
                callback(false)
            }
        }.start()
    }

    private fun installPRootSync(context: Context): Boolean {
        return try {
            extractAssets(context, "linux-rootfs.tar.gz", context.filesDir)
            File(context.filesDir, PROOT_MARKER).createNewFile()
            true
        } catch (e: Exception) {
            Timber.e(e, "PRoot installation failed")
            false
        }
    }

    fun installNodeJS(context: Context, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val success = installNodeJSSync(context)
                callback(success)
            } catch (e: Exception) {
                Timber.e(e, "Failed to install Node.js")
                callback(false)
            }
        }.start()
    }

    private fun installNodeJSSync(context: Context): Boolean {
        return try {
            val nodeDir = File(context.filesDir, "nodejs")
            if (!nodeDir.exists()) nodeDir.mkdirs()

            extractAssets(context, "nodejs", nodeDir)

            val nodeBinary = File(nodeDir, "bin/node")
            if (nodeBinary.exists()) {
                nodeBinary.setExecutable(true)
            }

            File(context.filesDir, NODEJS_MARKER).createNewFile()
            true
        } catch (e: Exception) {
            Timber.e(e, "Node.js installation failed")
            false
        }
    }

    fun installQingLong(context: Context, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val success = installQingLongSync(context)
                callback(success)
            } catch (e: Exception) {
                Timber.e(e, "Failed to install QingLong")
                callback(false)
            }
        }.start()
    }

    private fun installQingLongSync(context: Context): Boolean {
        return try {
            val qinglongDir = File(context.filesDir, "qinglong")
            if (!qinglongDir.exists()) qinglongDir.mkdirs()

            extractAssets(context, "qinglong", qinglongDir)

            File(context.filesDir, QINGLONG_MARKER).createNewFile()
            true
        } catch (e: Exception) {
            Timber.e(e, "QingLong installation failed")
            false
        }
    }

    private fun extractAssets(context: Context, assetName: String, targetDir: File): Boolean {
        return try {
            val assetManager = context.assets

            try {
                val files = assetManager.list(assetName)
                if (files != null) {
                    val dir = File(targetDir, assetName)
                    if (!dir.exists()) dir.mkdirs()

                    for (file in files) {
                        extractAssets(context, "$assetName/$file", targetDir)
                    }
                } else {
                    assetManager.open(assetName).use { input ->
                        File(targetDir, File(assetName).name).outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            } catch (e: Exception) {
                assetManager.open(assetName).use { input ->
                    File(targetDir, File(assetName).name).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to extract assets: $assetName")
            false
        }
    }
}
