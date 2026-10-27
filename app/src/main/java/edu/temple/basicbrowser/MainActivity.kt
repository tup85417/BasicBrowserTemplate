package edu.temple.basicbrowser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    private lateinit var urlEditText: EditText
    private lateinit var goButton: ImageButton
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlEditText = findViewById(R.id.urlEditText)
        goButton = findViewById(R.id.goButton)
        webView = findViewById(R.id.webView)

        // Allow browser to intercept hyperlink clicks
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                url?.run {
                    urlEditText.setText(this)
                }
            }
        }

        goButton.setOnClickListener {
            val url = parseUrl(urlEditText.text.toString())
            webView.loadUrl(url)
        }
    }

    fun parseUrl(url: String): String {
        return if (
            url.startsWith("https://") ||
            url.startsWith("http://")
        ) {
            url
        } else {
            "https://$url"
        }
    }
}