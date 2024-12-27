package com.sooum.android.ui.myprofile

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

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

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true // JavaScript 활성화
                settings.domStorageEnabled = true // DOM Storage 활성화
                webViewClient = WebViewClient() // 기본 WebViewClient 사용
                loadUrl(notionPage) // URL 로드
            }
        },
        modifier = Modifier.fillMaxSize() // 화면 크기 채우기
    )
}