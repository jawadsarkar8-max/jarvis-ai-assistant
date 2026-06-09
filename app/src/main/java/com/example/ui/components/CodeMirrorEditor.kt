package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CodeMirrorEditor(
    modifier: Modifier = Modifier,
    content: String,
    fileName: String,
    isReadOnly: Boolean,
    onContentChange: (String) -> Unit
) {
    val currentContent = remember { mutableStateOf(content) }
    val internalChange = remember { mutableStateOf(false) }

    LaunchedEffect(content) {
        currentContent.value = content
    }

    val escapedCode = remember(content) { escapeJsString(content) }
    val mode = remember(fileName) {
        when {
            fileName.endsWith(".css") -> "css"
            fileName.endsWith(".js") -> "javascript"
            fileName.endsWith(".json") -> "application/json"
            else -> "htmlmixed"
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            val webView = WebView(context)
            webView.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val jsCode = "setCode('${escapeJsString(content)}', '$mode', $isReadOnly);"
                        view?.evaluateJavascript(jsCode, null)
                    }
                }

                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onCodeChanged(newCode: String) {
                        webView.post {
                            if (currentContent.value != newCode) {
                                currentContent.value = newCode
                                internalChange.value = true
                                onContentChange(newCode)
                            }
                        }
                    }
                }, "AndroidInterface")

                loadDataWithBaseURL(
                    "https://editor.local/",
                    getEditorHtml(),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
            webView
        },
        update = { webView ->
            if (internalChange.value) {
                internalChange.value = false
            } else {
                val jsCode = "setCode('$escapedCode', '$mode', $isReadOnly);"
                webView.evaluateJavascript(jsCode, null)
            }
        }
    )
}

private fun escapeJsString(str: String): String {
    return str
        .replace("\\", "\\\\")
        .replace("'", "\\'")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\"", "\\\"")
}

private fun getEditorHtml(): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.13/codemirror.min.css" onerror="onFallback();">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.13/theme/material-darker.min.css">
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.13/codemirror.min.js" onerror="onFallback();"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.13/mode/xml/xml.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.13/mode/javascript/javascript.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.13/mode/css/css.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.13/mode/htmlmixed/htmlmixed.min.js"></script>
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    height: 100%;
                    background: #13151E;
                    color: #F0F2F6;
                    overflow: hidden;
                    font-family: monospace;
                }
                #fallback-editor {
                    width: 100%;
                    height: 100%;
                    background: #13151E;
                    color: #F0F2F6;
                    border: none;
                    outline: none;
                    padding: 10px;
                    font-size: 12px;
                    font-family: monospace;
                    resize: none;
                    display: none;
                    box-sizing: border-box;
                }
                .CodeMirror {
                    height: 100% !important;
                    font-family: 'JetBrains Mono', monospace !important;
                    font-size: 12px !important;
                    background: #13151E !important;
                    color: #F0F2F6 !important;
                }
                .CodeMirror-gutters {
                    background: #0D0F16 !important;
                    border-right: 1px solid #1E2235 !important;
                }
                .CodeMirror-linenumber {
                    color: #00F0FF !important;
                    opacity: 0.6;
                }
                .CodeMirror-cursor {
                    border-left: 2px solid #FF007F !important;
                }
                .CodeMirror-selected {
                    background: rgba(112, 0, 255, 0.3) !important;
                }
                ::-webkit-scrollbar {
                    width: 6px;
                    height: 6px;
                }
                ::-webkit-scrollbar-track {
                    background: #13151E;
                }
                ::-webkit-scrollbar-thumb {
                    background: #23273A;
                    border-radius: 3px;
                }
            </style>
        </head>
        <body>
            <textarea id="fallback-editor" oninput="onFallbackInput(this.value);"></textarea>
            <textarea id="editor"></textarea>
            
            <script>
                var useFallback = false;
                var editorInstance = null;
                var rawContent = "";
                var currentReadOnly = false;
                var modeName = "htmlmixed";
                var isSettingCode = false;

                function onFallback() {
                    if (!useFallback) {
                        useFallback = true;
                        document.getElementById("editor").style.display = "none";
                        var fall = document.getElementById("fallback-editor");
                        fall.style.display = "block";
                        fall.value = rawContent;
                        fall.readOnly = currentReadOnly;
                    }
                }

                function onFallbackInput(val) {
                    rawContent = val;
                    if (window.AndroidInterface) {
                        window.AndroidInterface.onCodeChanged(val);
                    }
                }

                setTimeout(function() {
                    if (typeof CodeMirror === "undefined") {
                        onFallback();
                    } else {
                        initCodeMirror();
                    }
                }, 800);

                function initCodeMirror() {
                    if (editorInstance) return;
                    var el = document.getElementById("editor");
                    if (!el) return;
                    
                    editorInstance = CodeMirror.fromTextArea(el, {
                        lineNumbers: true,
                        theme: "material-darker",
                        mode: modeName,
                        lineWrapping: true,
                        viewportMargin: Infinity,
                        readOnly: currentReadOnly ? "nocursor" : false
                    });

                    isSettingCode = true;
                    editorInstance.setValue(rawContent);
                    isSettingCode = false;

                    editorInstance.on("change", function(cm) {
                        if (isSettingCode) return;
                        rawContent = cm.getValue();
                        if (window.AndroidInterface) {
                            window.AndroidInterface.onCodeChanged(rawContent);
                        }
                    });
                }

                function setCode(code, mode, readOnly) {
                    rawContent = code;
                    currentReadOnly = readOnly;
                    modeName = mode;

                    if (useFallback) {
                        var fall = document.getElementById("fallback-editor");
                        if (fall.value !== code) {
                            fall.value = code;
                        }
                        fall.readOnly = readOnly;
                    } else if (editorInstance) {
                        if (editorInstance.getValue() !== code) {
                            isSettingCode = true;
                            editorInstance.setValue(code);
                            isSettingCode = false;
                        }
                        editorInstance.setOption("mode", mode);
                        editorInstance.setOption("readOnly", readOnly ? "nocursor" : false);
                    }
                }
            </script>
        </body>
        </html>
    """.trimIndent()
}
