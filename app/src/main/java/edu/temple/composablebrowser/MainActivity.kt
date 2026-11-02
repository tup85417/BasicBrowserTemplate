package edu.temple.composablebrowser

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import edu.temple.composablebrowser.ui.theme.ComposableWebBrowserTheme
import java.net.URI
import java.net.URISyntaxException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposableWebBrowserTheme {
                BrowserScreen()
            }
        }
    }
}

@Composable
fun BrowserScreen() {
    var urlText by rememberSaveable { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var requestedUrl by remember { mutableStateOf<String?>(null) }
    var requestNumber by remember { mutableStateOf(0) }
    val lastLoadedRequest = remember { intArrayOf(0) }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun navigate() {
        val normalized = normalizeWebUrl(urlText)
        if (normalized == null) {
            errorText = "Enter a valid http or https URL"
            return
        }
        errorText = null
        urlText = normalized
        requestedUrl = normalized
        requestNumber++
        keyboardController?.hide()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = urlText,
                    onValueChange = {
                        urlText = it
                        errorText = null
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("URL") },
                    placeholder = { Text("example.com") },
                    singleLine = true,
                    isError = errorText != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(onGo = { navigate() })
                )
                Button(onClick = { navigate() }, modifier = Modifier.heightIn(min = 56.dp)) {
                    Text("Go")
                }
            }

            if (errorText != null) {
                Text(
                    text = errorText!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                    }
                },
                update = { webView ->
                    val url = requestedUrl
                    if (url != null && lastLoadedRequest[0] != requestNumber) {
                        webView.loadUrl(url)
                        lastLoadedRequest[0] = requestNumber
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            )
        }
    }
}

/** Adds HTTPS when no scheme is supplied; rejects malformed and non-web URLs. */
fun normalizeWebUrl(input: String): String? {
    val typed = input.trim()
    if (typed.isEmpty()) return null

    val candidate = when {
        typed.startsWith("http://", ignoreCase = true) ||
            typed.startsWith("https://", ignoreCase = true) -> typed
        "://" in typed -> return null
        else -> "https://$typed"
    }

    return try {
        val uri = URI(candidate)
        if (uri.scheme?.lowercase() in setOf("http", "https") && !uri.host.isNullOrBlank()) {
            candidate
        } else {
            null
        }
    } catch (_: URISyntaxException) {
        null
    }
}
