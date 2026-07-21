package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SovereignViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BootScreen(
    viewModel: SovereignViewModel,
    modifier: Modifier = Modifier
) {
    val isBooting by viewModel.isBooting.collectAsState()
    val isBooted by viewModel.isBooted.collectAsState()
    val bootProgress by viewModel.bootProgress.collectAsState()
    val bootLogs by viewModel.bootLogs.collectAsState()
    val currentStage by viewModel.currentBootingStage.collectAsState()

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto-scroll dmesg logs to bottom as they arrive
    LaunchedEffect(bootLogs.size) {
        if (bootLogs.isNotEmpty()) {
            listState.animateScrollToItem(bootLogs.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isBooting && !isBooted) {
            // INITIAL POWER-ON PROMPT (Frosted Card)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .wrapContentHeight()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp)
                ) {
                    Text(
                        text = "SOVEREIGN ECOSYSTEM Ω",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFFE8E5DF),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "BOOTABLE REPRODUCTIVE ARCHITECTURE",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFFF00FF),
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Glowing gradient power button
                    Button(
                        onClick = { viewModel.triggerBoot() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .width(260.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF00D9FF), Color(0xFFFF00FF))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("power_on_button")
                    ) {
                        Text(
                            text = "◉  POWER SYSTEM ON",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Target Hardware: Moto G35 5G / ARM64\nSecured via Kademlia DHT Cryptography",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF8AA4BD).copy(alpha = 0.6f),
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // ACTIVE DMESG & BIOS LOGS (Frosted Terminal Container)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header console state bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isBooted) Color.Green else Color(0xFFFF5555))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBooted) "SYSTEM READY" else "LOADING CORE: $currentStage",
                                color = Color(0xFF00D9FF),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${(bootProgress * 100).toInt()}%",
                            color = Color(0xFF00D9FF),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tiny progress slider
                    LinearProgressIndicator(
                        progress = { bootProgress },
                        color = Color(0xFFFF00FF),
                        trackColor = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scrollable logs list
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(bootLogs) { log ->
                                val logColor = when {
                                    log.startsWith("📌") -> Color(0xFFFFAA00)
                                    log.startsWith("🔌") -> Color(0xFF00FF88)
                                    log.startsWith("   ├─ Verifying") -> Color(0xFF8AA4BD)
                                    log.contains("✅") -> Color(0xFF00FF88)
                                    log.contains("⚠️") -> Color(0xFFFF5555)
                                    else -> Color(0xFFE8E5DF)
                                }

                                Text(
                                    text = log,
                                    color = logColor,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
