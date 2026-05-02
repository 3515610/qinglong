package com.qinglong.panel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qinglong.panel.databinding.ActivityTerminalBinding

class TerminalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTerminalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTerminalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTerminal()
    }

    private fun setupTerminal() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
