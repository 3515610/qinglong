package com.qinglong.panel.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*
import java.io.*
import java.net.ServerSocket

class LocalServerManager(private val context: Context) {

    private var serverScope: CoroutineScope? = null
    private var isRunning = false
    private val mainHandler = Handler(Looper.getMainLooper())

    fun startServer(port: Int, onStart: (Boolean, String) -> Unit) {
        serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        
        serverScope?.launch {
            try {
                if (isPortAvailable(port)) {
                    startQingLongServer(port, onStart)
                } else {
                    postToMain(onStart, false, "端口 $port 已被占用")
                }
            } catch (e: Exception) {
                postToMain(onStart, false, "服务器启动失败：${e.message}")
            }
        }
    }

    private suspend fun startQingLongServer(port: Int, onStart: (Boolean, String) -> Unit) {
        val qinglongDir = File(context.filesDir, "qinglong")
        
        if (!qinglongDir.exists()) {
            postToMain(onStart, false, "青龙面板未安装")
            return
        }

        val startScript = File(qinglongDir, "start.sh")
        if (!startScript.exists()) {
            createStartScript(qinglongDir, port)
        }

        val env = createEnvironment()
        
        val processBuilder = ProcessBuilder(
            "sh", startScript.absolutePath
        )
        processBuilder.directory(qinglongDir)
        processBuilder.environment().putAll(env)
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        isRunning = true

        postToMain(onStart, true, "服务器已启动")

        process.inputStream.bufferedReader().use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null && isRunning) {
                // Read server output
            }
        }
    }

    private fun postToMain(callback: (Boolean, String) -> Unit, success: Boolean, message: String) {
        mainHandler.post { callback(success, message) }
    }

    private fun createStartScript(dir: File, port: Int) {
        val script = """
#!/bin/bash
cd "${dir.absolutePath}"

export PATH="${context.filesDir}/nodejs/bin:\$PATH"
export NODE_PATH="${context.filesDir}/nodejs/lib/node_modules"

echo "Starting QingLong Panel on port $port..."

node main.js --port $port &
echo \$! > /tmp/qinglong.pid

wait \$!
"""
        File(dir, "start.sh").writeText(script)
        File(dir, "start.sh").setExecutable(true)
    }

    private fun createEnvironment(): Map<String, String> {
        return mapOf(
            "PATH" to "${context.filesDir}/nodejs/bin:${System.getenv("PATH")}",
            "NODE_PATH" to "${context.filesDir}/nodejs/lib/node_modules",
            "QINGLONG_DIR" to "${context.filesDir}/qinglong",
            "HOME" to context.filesDir.absolutePath
        )
    }

    private fun isPortAvailable(port: Int): Boolean {
        return try {
            ServerSocket(port).use { true }
        } catch (e: Exception) {
            false
        }
    }

    fun stopServer() {
        isRunning = false
        serverScope?.cancel()
        
        try {
            val pidFile = File(context.filesDir, "qinglong/pid")
            if (pidFile.exists()) {
                val pid = pidFile.readText().trim()
                Runtime.getRuntime().exec(arrayOf("kill", pid))
            }
        } catch (e: Exception) {
            // Ignore
        }
    }
}
