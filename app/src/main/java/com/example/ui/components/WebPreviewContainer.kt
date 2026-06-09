package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.WebPage
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPreviewContainer(
    modifier: Modifier = Modifier,
    pages: List<WebPage>,
    selectedPage: WebPage?,
    onPageNavigated: (WebPage) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    
    // Key-value map of fileName -> content for high-speed intercepts
    val pagesMap = remember(pages) {
        pages.associateBy { it.fileName }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        if (selectedPage == null || pages.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Engineering workspace is empty // Initiate synthesis",
                    color = Color.Gray
                )
            }
        } else {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }

                            // Capture local transitions within Web environment
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val urlStr = request?.url?.toString() ?: ""
                                if (urlStr.startsWith("https://jarvis.local/")) {
                                    var fileName = urlStr.removePrefix("https://jarvis.local/")
                                    if (fileName.contains("#")) {
                                        fileName = fileName.substringBefore("#")
                                    }
                                    if (fileName.contains("?")) {
                                        fileName = fileName.substringBefore("?")
                                    }
                                    val page = pagesMap[fileName]
                                    if (page != null) {
                                        onPageNavigated(page)
                                        return false // Proceed loading locally
                                    }
                                }
                                return false
                            }

                            // Inject compiled assets on-demand
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val urlStr = request?.url?.toString() ?: ""
                                if (urlStr.startsWith("https://jarvis.local/")) {
                                    var fileName = urlStr.removePrefix("https://jarvis.local/")
                                    if (fileName.contains("#")) {
                                        fileName = fileName.substringBefore("#")
                                    }
                                    if (fileName.contains("?")) {
                                        fileName = fileName.substringBefore("?")
                                    }
                                    val match = pagesMap[fileName]
                                    if (match != null) {
                                        val mimeType = when {
                                            fileName.endsWith(".css") -> "text/css"
                                            fileName.endsWith(".js") -> "application/javascript"
                                            fileName.endsWith(".json") -> "application/json"
                                            else -> "text/html"
                                        }
                                        
                                        return WebResourceResponse(
                                            mimeType,
                                            "UTF-8",
                                            ByteArrayInputStream(match.content.toByteArray(StandardCharsets.UTF_8))
                                        )
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }
                    }
                },
                update = { webView ->
                    // Whenever selectedPage or files mutate, load the webpage base context
                    val currentUrl = webView.url
                    val expectedUrl = "https://jarvis.local/${selectedPage.fileName}"
                    val tagContent = webView.tag as? String
                    
                    if (currentUrl != expectedUrl || tagContent != selectedPage.content) {
                        webView.tag = selectedPage.content
                        webView.loadDataWithBaseURL(
                            "https://jarvis.local/",
                            selectedPage.content,
                            "text/html",
                            "UTF-8",
                            expectedUrl
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00F0FF))
            }
        }
    }
}
