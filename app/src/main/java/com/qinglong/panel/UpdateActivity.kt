package com.qinglong.panel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qinglong.panel.adapter.UpdateAdapter
import com.qinglong.panel.databinding.ActivityUpdateBinding
import com.qinglong.panel.utils.QingLongUpdater

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var updateAdapter: UpdateAdapter
    private val updater = QingLongUpdater(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        checkForUpdates()
    }

    private fun setupRecyclerView() {
        updateAdapter = UpdateAdapter()
        binding.recyclerViewUpdates.apply {
            layoutManager = LinearLayoutManager(this@UpdateActivity)
            adapter = updateAdapter
        }
    }

    private fun setupListeners() {
        binding.btnCheckUpdate.setOnClickListener {
            checkForUpdates()
        }

        binding.btnUpdateAll.setOnClickListener {
            updateAll()
        }
    }

    private fun checkForUpdates() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.tvStatus.text = "正在检查更新..."

        updater.checkForUpdates { result ->
            runOnUiThread {
                binding.progressBar.visibility = android.view.View.GONE
                when (result) {
                    is QingLongUpdater.UpdateResult.Success -> {
                        if (result.updates.isEmpty()) {
                            binding.tvStatus.text = "当前已是最新版本"
                        } else {
                            binding.tvStatus.text = "发现 ${result.updates.size} 个更新"
                            updateAdapter.submitList(result.updates)
                        }
                    }
                    is QingLongUpdater.UpdateResult.Error -> {
                        binding.tvStatus.text = "检查更新失败：${result.message}"
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateAll() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.tvStatus.text = "正在更新..."

        updater.updateAll { success, message ->
            runOnUiThread {
                binding.progressBar.visibility = android.view.View.GONE
                if (success) {
                    binding.tvStatus.text = "更新完成"
                    Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
                    checkForUpdates()
                } else {
                    binding.tvStatus.text = "更新失败：$message"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
