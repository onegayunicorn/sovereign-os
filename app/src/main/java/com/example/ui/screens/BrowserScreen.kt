package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.SovereignViewModel

@Composable
fun BrowserScreen(
    viewModel: SovereignViewModel,
    modifier: Modifier = Modifier
) {
    val isLightTheme by viewModel.isLightTheme.collectAsState()
    val packages by viewModel.packages.collectAsState()

    val isBrowserInstalled = remember(packages) {
        packages.find { it.name == "sovereign-browser" }?.isInstalled == true
    }

    // Color theme bindings
    val textColor = if (isLightTheme) Color(0xFF1E293B) else Color(0xFFE8E5DF)
    val cardBg = if (isLightTheme) Color.White else Color.White.copy(alpha = 0.05f)
    val cardBorder = if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f)
    val innerBg = if (isLightTheme) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.35f)
    val accentColor = if (isLightTheme) Color(0xFF007799) else Color(0xFF00D9FF)

    if (!isBrowserInstalled) {
        // Enforce installing the package first
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.widthIn(max = 500.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PublicOff,
                        contentDescription = "Uninstalled Package",
                        tint = Color(0xFFFF5555),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🌐 WEB BROWSER NOT INSTALLED",
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("browser_uninstalled_title")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sovereign OS Sandbox Browser requires the 'sovereign-browser' package to be loaded into volatile memory structures.\n\nYou can install this package in the Settings tab Software Center, or execute package installation command from the Terminal:\n\n`pkg install sovereign-browser`",
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.installPackage("sovereign-browser") },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("install_browser_from_uninstalled_card")
                    ) {
                        Text(
                            text = "QUICK INSTALL BROWSER PACKAGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isLightTheme) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    } else {
        // Render fully functioning browser
        var webViewRef by remember { mutableStateOf<WebView?>(null) }
        var currentUrlInput by remember { mutableStateOf("https://www.google.com") }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Browser Address Bar & Navigation Buttons
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        enabled = webViewRef?.canGoBack() == true,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = accentColor)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        enabled = webViewRef?.canGoForward() == true,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = accentColor)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Forward", modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { webViewRef?.reload() },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = accentColor)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp))
                    }

                    OutlinedTextField(
                        value = currentUrlInput,
                        onValueChange = { currentUrlInput = it },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = innerBg,
                            unfocusedContainerColor = innerBg,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = cardBorder.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("browser_address_input")
                    )

                    Button(
                        onClick = {
                            var targetUrl = currentUrlInput.trim()
                            if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                                targetUrl = "https://$targetUrl"
                            }
                            webViewRef?.loadUrl(targetUrl)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "GO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isLightTheme) Color.White else Color.Black
                        )
                    }
                }
            }

            // Interactive Web View Render Pane
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                ) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        if (url != null) {
                                            currentUrlInput = url
                                        }
                                    }
                                }
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.useWideViewPort = true
                                settings.loadWithOverviewMode = true
                                loadUrl(currentUrlInput)
                                webViewRef = this
                            }
                        },
                        update = { webView ->
                            webViewRef = webView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
