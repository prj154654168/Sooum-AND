package com.sooum.android.ui.myprofile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.sooum.android.SooumApplication

@Composable
fun NotionPageScreen() {
    var url by remember { mutableStateOf(SooumApplication().getVariable("notionUrl")) }
    WebView(
        state = rememberWebViewState(url = url),
        client = AccompanistWebViewClient(),
        chromeClient = AccompanistWebChromeClient(),
        onCreated = { webView ->
            with(webView) {
                settings.run {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    javaScriptCanOpenWindowsAutomatically = false
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}