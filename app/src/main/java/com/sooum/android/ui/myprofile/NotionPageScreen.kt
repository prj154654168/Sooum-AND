package com.sooum.android.ui.myprofile

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewState

@Composable
fun NotionPageScreen(url: String) {
    val notionPage = when (url) {
        "개인정보처리방침" -> {
            "https://mewing-space-6d3.notion.site/44e378c9d11d45159859492434b6b128"
        }
        "서비스 이용약관" -> {
            "https://mewing-space-6d3.notion.site/3f92380d536a4b569921d2809ed147ef"
        }
        else -> {
            "https://mewing-space-6d3.notion.site/45d151f68ba74b23b24483ad8b2662b4"
        }
    }
    WebView(
        state = rememberWebViewState(url = notionPage),
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
//    AndroidView(
//        factory = { context ->
//            WebView(context).apply {
//                settings.javaScriptEnabled = true // JavaScript 활성화
//                settings.domStorageEnabled = true // DOM Storage 활성화
//                webViewClient = AccompanistWebViewClient() // 기본 WebViewClient 사용
//                loadUrl(notionPage) // URL 로드
//            }
//        },
//        modifier = Modifier.fillMaxSize().background(Color.Black) // 화면 크기 채우기
//    )
}