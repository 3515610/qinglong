package com.qinglong.panel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.qinglong.panel.databinding.ActivityMainBinding
import com.qinglong.panel.service.QingLongForegroundService
import com.qinglong.panel.utils.EnvironmentChecker

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isEnvironmentReady = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            startEnvironmentInitialization()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkPermissions()
    }

    private fun setupUI() {
        binding.btnOpenPanel.setOnClickListener {
            if (isEnvironmentReady) {
                openQingLongPanel()
            } else {
                Toast.makeText(this, "环境初始化中，请稍候...", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCheckUpdate.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            Toast.makeText(this, "设置功能开发中...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            startEnvironmentInitialization()
        } else {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun startEnvironmentInitialization() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvStatus.text = "正在检查环境..."

        if (!EnvironmentChecker.isPRootInstalled(this)) {
            binding.tvStatus.text = "正在安装Linux环境..."
            EnvironmentChecker.installPRoot(this) { success ->
                if (success) {
                    checkNodeJS()
                } else {
                    showError("Linux环境安装失败")
                }
            }
        } else {
            checkNodeJS()
        }
    }

    private fun checkNodeJS() {
        binding.tvStatus.text = "正在检查Node.js..."

        if (!EnvironmentChecker.isNodeJSInstalled(this)) {
            binding.tvStatus.text = "正在安装Node.js..."
            EnvironmentChecker.installNodeJS(this) { success ->
                if (success) {
                    checkQingLong()
                } else {
                    showError("Node.js安装失败")
                }
            }
        } else {
            checkQingLong()
        }
    }

    private fun checkQingLong() {
        binding.tvStatus.text = "正在检查青龙面板..."

        if (!EnvironmentChecker.isQingLongInstalled(this)) {
            binding.tvStatus.text = "正在部署青龙面板..."
            EnvironmentChecker.installQingLong(this) { success ->
                if (success) {
                    environmentReady()
                } else {
                    showError("青龙面板部署失败")
                }
            }
        } else {
            environmentReady()
        }
    }

    private fun environmentReady() {
        isEnvironmentReady = true
        binding.progressBar.visibility = View.GONE
        binding.tvStatus.text = "环境就绪"
        binding.btnOpenPanel.isEnabled = true

        startForegroundService()
    }

    private fun startForegroundService() {
        val serviceIntent = Intent(this, QingLongForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun openQingLongPanel() {
        val intent = Intent(this, WebViewActivity::class.java)
        startActivity(intent)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.tvStatus.text = message
        binding.btnOpenPanel.isEnabled = false

        AlertDialog.Builder(this)
            .setTitle("错误")
            .setMessage(message)
            .setPositiveButton("重试") { _, _ ->
                startEnvironmentInitialization()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("权限不足")
            .setMessage("应用需要相关权限才能正常运行")
            .setPositiveButton("重新授权") { _, _ ->
                checkPermissions()
            }
            .setNegativeButton("退出") { _, _ ->
                finish()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (isEnvironmentReady) {
            binding.tvStatus.text = "环境就绪"
        }
    }
}
