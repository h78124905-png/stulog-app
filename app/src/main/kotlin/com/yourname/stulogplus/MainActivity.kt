package com.yourname.stulogplus

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var pendingBackupJson: String? = null
    private val createBackupRequest = 7301

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(11, 12, 14)
        window.navigationBarColor = Color.rgb(11, 12, 14)
        window.decorView.systemUiVisibility = 0

        webView = WebView(this).apply {
            setBackgroundColor(Color.rgb(11, 12, 14))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.setSupportZoom(false)
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            webChromeClient = WebChromeClient()
            addJavascriptInterface(BackupBridge(), "AndroidBridge")
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val url = request.url.toString()
                    if (url.startsWith("file:///android_asset/")) return false
                    if (url.startsWith("https://") || url.startsWith("http://")) {
                        return try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            true
                        } catch (_: Exception) {
                            false
                        }
                    }
                    return true
                }
            }
        }
        setContentView(webView)
        if (savedInstanceState == null) webView.loadUrl("file:///android_asset/index.html")
        else webView.restoreState(savedInstanceState)
    }

    private inner class BackupBridge {
        @JavascriptInterface
        fun saveBackup(json: String) {
            runOnUiThread {
                pendingBackupJson = json
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "sutalog_backup.json")
                }
                try {
                    startActivityForResult(intent, createBackupRequest)
                } catch (_: Exception) {
                    pendingBackupJson = null
                    Toast.makeText(this@MainActivity, "保存先を開けませんでした", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Android API, retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != createBackupRequest) return
        val json = pendingBackupJson
        pendingBackupJson = null
        if (resultCode != RESULT_OK || data?.data == null || json == null) return
        try {
            val stream = contentResolver.openOutputStream(data.data!!) ?: throw IllegalStateException("Cannot open output stream")
            stream.bufferedWriter(Charsets.UTF_8).use { it.write(json) }
            Toast.makeText(this, "バックアップを保存しました", Toast.LENGTH_LONG).show()
        } catch (_: Exception) {
            Toast.makeText(this, "バックアップの保存に失敗しました", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            (webView.parent as? android.view.ViewGroup)?.removeView(webView)
            webView.destroy()
        }
        super.onDestroy()
    }
}
