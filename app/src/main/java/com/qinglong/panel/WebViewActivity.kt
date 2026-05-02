package com.qinglong.panel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qinglong.panel.databinding.ActivityWebviewBinding
import com.qinglong.panel.utils.LocalServerManager

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding
    private lateinit var localServerManager: LocalServerManager

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localServerManager = LocalServerManager(this)

        setupWebView()
        loadQingLongPanel()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webView = binding.webView

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(false)
            builtInZoomControls = false
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    showError("无法连接到青龙面板")
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressBar.progress = newProgress
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let {
                    android.util.Log.d("WebView", it.message())
                }
                return super.onConsoleMessage(consoleMessage)
            }
        }

        binding.webView.addJavascriptInterface(WebAppInterface(this), "Android")
    }

    private fun loadQingLongPanel() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvLoadingStatus.text = "正在启动本地服务器..."

        localServerManager.startServer(
            port = 5700,
            onStart = { success, message ->
                runOnUiThread {
                    if (success) {
                        binding.tvLoadingStatus.text = "服务器已启动，正在加载面板..."
                        val url = "http://127.0.0.1:5700"
                        binding.webView.loadUrl(url)
                    } else {
                        showError(message)
                    }
                }
            }
        )
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.tvLoadingStatus.text = message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
        localServerManager.stopServer()
    }
}

class WebAppInterface(private val context: WebViewActivity) {
    @JavascriptInterface
    fun openTerminal() {
        context.startActivity(Intent(context, TerminalActivity::class.java))
    }

    @JavascriptInterface
    fun openUpdate() {
        context.startActivity(Intent(context, UpdateActivity::class.java))
    }
}
