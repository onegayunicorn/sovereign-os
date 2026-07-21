package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SovereignViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DashboardScreen(
    viewModel: SovereignViewModel,
    modifier: Modifier = Modifier
) {
    val coherence by viewModel.coherence.collectAsState()
    val coherenceHistory by viewModel.coherenceHistory.collectAsState()
    val timeCrystalFreq by viewModel.timeCrystalFrequency.collectAsState()
    val timeCrystalRad by viewModel.timeCrystalRadius.collectAsState()
    val schumannLock by viewModel.schumannLock.collectAsState()
    val peers by viewModel.peerCount.collectAsState()
    val consensus by viewModel.consensusCount.collectAsState()
    val barometerHpa by viewModel.barometerHpa.collectAsState()
    val bciAttention by viewModel.bciAttention.collectAsState()
    val npuLoad by viewModel.npuLoad.collectAsState()
    val consoleLogs by viewModel.consoleLogs.collectAsState()
    val isLightTheme by viewModel.isLightTheme.collectAsState()

    // BCI Subsystem State
    val bciIsInstalled by viewModel.bciIsInstalled.collectAsState()
    val bciHeartRate by viewModel.bciHeartRate.collectAsState()
    val bciSpo2 by viewModel.bciSpo2.collectAsState()
    val bciSkinTemp by viewModel.bciSkinTemp.collectAsState()
    val bciGsr by viewModel.bciGsr.collectAsState()
    val bciCognitiveLoad by viewModel.bciCognitiveLoad.collectAsState()
    val bciEmotionalState by viewModel.bciEmotionalState.collectAsState()
    val bciActiveCommand by viewModel.bciActiveCommand.collectAsState()
    val bciSignature by viewModel.bciSignature.collectAsState()
    val bciEegChannels by viewModel.bciEegChannels.collectAsState()

    // Face Scan & Bio Security State
    val faceBioQuality by viewModel.faceBioQuality.collectAsState()
    val faceBioLivenessState by viewModel.faceBioLivenessState.collectAsState()
    val faceBioLivenessConfidence by viewModel.faceBioLivenessConfidence.collectAsState()
    val faceBioFusionScore by viewModel.faceBioFusionScore.collectAsState()
    val faceBioDecision by viewModel.faceBioDecision.collectAsState()
    val faceBioIrisScore by viewModel.faceBioIrisScore.collectAsState()
    val faceBioVoiceScore by viewModel.faceBioVoiceScore.collectAsState()
    val faceBioHeartRateScore by viewModel.faceBioHeartRateScore.collectAsState()
    val faceBioSkinTextureScore by viewModel.faceBioSkinTextureScore.collectAsState()
    val faceBioVeinPatternScore by viewModel.faceBioVeinPatternScore.collectAsState()
    val faceBioPhi5Signature by viewModel.faceBioPhi5Signature.collectAsState()
    val faceBioAuditLogs by viewModel.faceBioAuditLogs.collectAsState()
    val faceBioEnrolledUsers by viewModel.faceBioEnrolledUsers.collectAsState()

    // Tab switcher: 0 = Core Metrics, 1 = Samsung A17 BCI, 2 = Face Scan & Bio Security
    var selectedDashboardTab by remember { mutableStateOf(0) }

    // Color schema adaptive to Light & Dark options
    val textColor = if (isLightTheme) Color(0xFF1E293B) else Color(0xFFE8E5DF)
    val subTextColor = if (isLightTheme) Color(0xFF475569) else Color(0xFF8AA4BD)
    val cardBg = if (isLightTheme) Color.White else Color.White.copy(alpha = 0.05f)
    val cardBorder = if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f)
    val innerBg = if (isLightTheme) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.35f)
    val accentColor = if (isLightTheme) Color(0xFF007799) else Color(0xFF00D9FF)
    val crystalAccent = if (isLightTheme) Color(0xFFC026D3) else Color(0xFFFF00FF)
    val schumannColor = if (isLightTheme) Color(0xFF166534) else Color(0xFF00FF88)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // TOP CONTROL SEGMENTED BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tab 0: Core Metrics
                Button(
                    onClick = { selectedDashboardTab = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDashboardTab == 0) accentColor else cardBg,
                        contentColor = if (selectedDashboardTab == 0) Color.Black else textColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Core Metrics",
                        modifier = Modifier.size(16.dp),
                        tint = if (selectedDashboardTab == 0) Color.Black else textColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CORE METRICS", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                // Tab 1: Samsung A17 BCI Telemetry
                Button(
                    onClick = { selectedDashboardTab = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDashboardTab == 1) crystalAccent else cardBg,
                        contentColor = if (selectedDashboardTab == 1) Color.Black else textColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "BCI Telemetry",
                        modifier = Modifier.size(16.dp),
                        tint = if (selectedDashboardTab == 1) Color.Black else textColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SAMSUNG A17 BCI", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    if (!bciIsInstalled) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            modifier = Modifier.size(12.dp),
                            tint = if (selectedDashboardTab == 1) Color.Black.copy(alpha = 0.6f) else subTextColor
                        )
                    }
                }

                // Tab 2: Full Face Scan & Bio Security Module
                Button(
                    onClick = { selectedDashboardTab = 2 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDashboardTab == 2) Color(0xFF00FF88) else cardBg,
                        contentColor = if (selectedDashboardTab == 2) Color.Black else textColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Face Scan & Bio Security",
                        modifier = Modifier.size(16.dp),
                        tint = if (selectedDashboardTab == 2) Color.Black else textColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("FACE SCAN & BIO MODULE", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        if (selectedDashboardTab == 0) {
            // Core System Metrics Dashboard Tab
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // LEFT COLUMN: Time Crystal & P2P Mesh Controls
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // TIME CRYSTAL CONTROL
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⏰ TIME CRYSTAL STABILISER",
                                    color = crystalAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(crystalAccent.copy(alpha = 0.15f))
                                        .border(1.dp, crystalAccent.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LOCKED",
                                        color = crystalAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Pulsing Time Crystal canvas graphic
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    val infiniteTransition = rememberInfiniteTransition(label = "crystal")
                                    val pulse by infiniteTransition.animateFloat(
                                        initialValue = 0.85f,
                                        targetValue = 1.2f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1200, easing = EaseInOutSine),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "pulse"
                                    )
                                    val rotation by infiniteTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 360f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(6000, easing = LinearEasing),
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "rotation"
                                    )

                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val cX = size.width / 2f
                                        val cY = size.height / 2f
                                        val baseRadius = 24f * pulse

                                        // Draw rotating wireframe diamond
                                        val rads = Math.toRadians(rotation.toDouble())
                                        val cosR = cos(rads).toFloat()
                                        val sinR = sin(rads).toFloat()

                                        val top = Offset(cX, cY - baseRadius * 1.5f)
                                        val bottom = Offset(cX, cY + baseRadius * 1.5f)
                                        val left = Offset(cX - baseRadius * cosR, cY - baseRadius * sinR * 0.4f)
                                        val right = Offset(cX + baseRadius * cosR, cY + baseRadius * sinR * 0.4f)

                                        drawLine(crystalAccent, top, left, strokeWidth = 2f)
                                        drawLine(crystalAccent, top, right, strokeWidth = 2f)
                                        drawLine(crystalAccent, bottom, left, strokeWidth = 2f)
                                        drawLine(crystalAccent, bottom, right, strokeWidth = 2f)
                                        drawLine(crystalAccent.copy(alpha = 0.5f), left, right, strokeWidth = 1f)

                                        drawCircle(crystalAccent, radius = 3f, center = top)
                                        drawCircle(crystalAccent, radius = 3f, center = bottom)
                                        drawCircle(textColor, radius = 4f, center = left)
                                        drawCircle(textColor, radius = 4f, center = right)
                                    }
                                }

                                // Time crystal parameters
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    MetricItem(label = "Target Resonance", value = "7.830 Hz", textColor = textColor, subTextColor = subTextColor)
                                    MetricItem(label = "Active Frequency", value = "${String.format("%.3f", timeCrystalFreq)} Hz", textColor = textColor, subTextColor = subTextColor)
                                    MetricItem(label = "Coherence Radius", value = String.format("%.6f", timeCrystalRad), textColor = textColor, subTextColor = subTextColor)
                                    MetricItem(label = "Phase Drift", value = "< 0.0004%", textColor = textColor, subTextColor = subTextColor)
                                }
                            }
                        }
                    }

                    // P2P MESH NETWORK MODULE
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🌐 P2P KADEMLIA DHT MESH",
                                    color = accentColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(accentColor.copy(alpha = 0.15f))
                                        .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "BFT VALIDATED",
                                        color = accentColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            GridTwoByTwo(
                                item1 = { MetricItem(label = "DHT Peer Capacity", value = "$peers / 64 Nodes", textColor = textColor, subTextColor = subTextColor) },
                                item2 = { MetricItem(label = "Consensus Level", value = "$consensus / $peers Peers", textColor = textColor, subTextColor = subTextColor) },
                                item3 = { MetricItem(label = "Quantum Security", value = "NTRU-Prime", textColor = textColor, subTextColor = subTextColor) },
                                item4 = { MetricItem(label = "Average Latency", value = "2.8 ms", textColor = textColor, subTextColor = subTextColor) }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { consensus.toFloat() / peers.toFloat() },
                                color = accentColor,
                                trackColor = if (isLightTheme) Color(0xFFCBD5E1) else Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                // RIGHT COLUMN: Live Coherence Graph, Sensors & Scrolling Console
                Column(
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // COHERENCE Φ GRAPH CARD
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "📈 COHERENCE COEFFICIENT (Φ)",
                                        color = accentColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Target: 0.9990  Current: ${String.format("%.4f", coherence)}",
                                        color = subTextColor,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    text = "Φ = ${String.format("%.4f", coherence)}",
                                    color = if (coherence > 0.98f) schumannColor else Color(0xFFFFAA00),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(innerBg)
                                    .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height
                                    val maxPoints = 30
                                    val points = coherenceHistory

                                    if (points.isNotEmpty()) {
                                        val path = Path()
                                        val stepX = w / (maxPoints - 1)

                                        val minVal = 0.90f
                                        val maxVal = 1.00f
                                        val range = maxVal - minVal

                                        points.forEachIndexed { idx, valF ->
                                            val pctY = (valF - minVal) / range
                                            val cx = idx * stepX
                                            val cy = h - (pctY * h)

                                            if (idx == 0) {
                                                path.moveTo(cx, cy)
                                            } else {
                                                path.lineTo(cx, cy)
                                            }
                                        }

                                        drawPath(
                                            path = path,
                                            color = accentColor,
                                            style = Stroke(width = 3f)
                                        )

                                        val fillPath = Path().apply {
                                            addPath(path)
                                            lineTo(w, h)
                                            lineTo(0f, h)
                                            close()
                                        }
                                        drawPath(
                                            path = fillPath,
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    accentColor.copy(alpha = 0.15f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // LOWER SUB-PANEL: Hardware Sensors & active log
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // HARDWARE SENSORS GAUGE
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, cardBorder),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🌡️ HARDWARE SENSORS",
                                    color = Color(0xFFFFAA00),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    SensorIndicator(
                                        label = "Barometer",
                                        value = "${String.format("%.2f", barometerHpa)} hPa",
                                        barPct = (barometerHpa - 1000f) / 30f,
                                        isLightTheme = isLightTheme,
                                        subTextColor = subTextColor,
                                        textColor = textColor
                                    )
                                    SensorIndicator(
                                        label = "BCI Attention",
                                        value = String.format("%.2f", bciAttention),
                                        barPct = bciAttention,
                                        isLightTheme = isLightTheme,
                                        subTextColor = subTextColor,
                                        textColor = textColor
                                    )
                                    SensorIndicator(
                                        label = "NPU Load",
                                        value = "$npuLoad%",
                                        barPct = npuLoad / 100f,
                                        isLightTheme = isLightTheme,
                                        subTextColor = subTextColor,
                                        textColor = textColor
                                    )
                                }
                            }
                        }

                        // SCROLLING CONSOLE LOGS
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, cardBorder),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "📋 SYSTEM MONITOR LOG",
                                    color = subTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(innerBg)
                                        .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                        .padding(12.dp)
                                ) {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(consoleLogs) { log ->
                                            Text(
                                                text = log,
                                                color = schumannColor.copy(alpha = 0.85f),
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                lineHeight = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedDashboardTab == 1) {
            // Samsung A17 BCI Telemetry Tab
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (!bciIsInstalled) {
                    LockedBciPanel(cardBg, cardBorder, crystalAccent, textColor, subTextColor, innerBg, viewModel)
                } else {
                    ActiveBciDashboard(
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        accentColor = crystalAccent,
                        textColor = textColor,
                        subTextColor = subTextColor,
                        innerBg = innerBg,
                        schumannColor = schumannColor,
                        standardAccentColor = accentColor,
                        bciHeartRate = bciHeartRate,
                        bciSpo2 = bciSpo2,
                        bciSkinTemp = bciSkinTemp,
                        bciGsr = bciGsr,
                        bciCognitiveLoad = bciCognitiveLoad,
                        bciEmotionalState = bciEmotionalState,
                        bciActiveCommand = bciActiveCommand,
                        bciSignature = bciSignature,
                        bciEegChannels = bciEegChannels,
                        bciAttention = bciAttention,
                        isLightTheme = isLightTheme
                    )
                }
            }
        } else {
            // Full Face Scan & Bio Security Module Tab
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                FaceScanBioDashboard(
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    accentColor = Color(0xFF00FF88),
                    textColor = textColor,
                    subTextColor = subTextColor,
                    innerBg = innerBg,
                    schumannColor = schumannColor,
                    standardAccentColor = accentColor,
                    faceBioQuality = faceBioQuality,
                    faceBioLivenessState = faceBioLivenessState,
                    faceBioLivenessConfidence = faceBioLivenessConfidence,
                    faceBioFusionScore = faceBioFusionScore,
                    faceBioDecision = faceBioDecision,
                    faceBioIrisScore = faceBioIrisScore,
                    faceBioVoiceScore = faceBioVoiceScore,
                    faceBioHeartRateScore = faceBioHeartRateScore,
                    faceBioSkinTextureScore = faceBioSkinTextureScore,
                    faceBioVeinPatternScore = faceBioVeinPatternScore,
                    faceBioPhi5Signature = faceBioPhi5Signature,
                    faceBioAuditLogs = faceBioAuditLogs,
                    faceBioEnrolledUsers = faceBioEnrolledUsers,
                    isLightTheme = isLightTheme,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun LockedBciPanel(
    cardBg: Color,
    cardBorder: Color,
    accentColor: Color,
    textColor: Color,
    subTextColor: Color,
    innerBg: Color,
    viewModel: SovereignViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(24.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.widthIn(max = 500.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked Connection",
                modifier = Modifier.size(64.dp),
                tint = accentColor
            )

            Text(
                text = "⚡ QUANTUM BCI LINK OFFLINE",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = "The Samsung A17 BCI sub-modules are not telemetered. A direct physical or quantum bridge is required to uplink the neural twin data.",
                color = subTextColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(innerBg)
                    .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📋 REQUIRED INTEGRATION PROCEDURES:",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "1. Switch to the OPERATIONS console tab.\n" +
                               "2. Ensure you are logged in as operator@sovereign.\n" +
                               "3. Run automated installation script:\n" +
                               "   $ sh install.sh\n" +
                               "4. Alternative package installation command:\n" +
                               "   $ sudo pkg install samsung-a17-bci",
                        color = textColor.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }

            Button(
                onClick = { viewModel.selectTab(com.example.ui.SovereignTab.OPERATIONS) },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("OPEN OPERATIONS CONSOLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun ActiveBciDashboard(
    cardBg: Color,
    cardBorder: Color,
    accentColor: Color,
    textColor: Color,
    subTextColor: Color,
    innerBg: Color,
    schumannColor: Color,
    standardAccentColor: Color,
    bciHeartRate: Int,
    bciSpo2: Int,
    bciSkinTemp: Float,
    bciGsr: Float,
    bciCognitiveLoad: Int,
    bciEmotionalState: String,
    bciActiveCommand: String,
    bciSignature: String,
    bciEegChannels: List<Float>,
    bciAttention: Float,
    isLightTheme: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // LEFT COLUMN: Profile and Biometric Baselines
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // BIO-TWIN PROFILE CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1.3f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👤 BIO-DIGITAL TWIN PROFILE",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(schumannColor.copy(alpha = 0.15f))
                                .border(1.dp, schumannColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MIRRORED",
                                color = schumannColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Avatar Representation
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(innerBg)
                                .border(1.dp, cardBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = "Commander Tyrone",
                                modifier = Modifier.size(36.dp),
                                tint = accentColor
                            )
                        }

                        Column {
                            Text(
                                text = "Commander Tyrone Ω",
                                color = textColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Origin Node: Year 2036",
                                color = subTextColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // BCI Indicators
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SensorIndicator(
                            label = "Attention Level",
                            value = String.format("%.2f", bciAttention),
                            barPct = bciAttention,
                            isLightTheme = isLightTheme,
                            subTextColor = subTextColor,
                            textColor = textColor
                        )
                        SensorIndicator(
                            label = "Cognitive Load",
                            value = "$bciCognitiveLoad%",
                            barPct = bciCognitiveLoad / 100f,
                            isLightTheme = isLightTheme,
                            subTextColor = subTextColor,
                            textColor = textColor
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Emotional State", color = subTextColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text(bciEmotionalState, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Detected Intent", color = subTextColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text(bciActiveCommand, color = schumannColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // BIOMETRIC BASELINES MATRIX
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🧬 BIOMETRIC MATRIX FEED",
                        color = standardAccentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GridTwoByTwo(
                        item1 = { MetricItem(label = "Heart Rate", value = "$bciHeartRate BPM", textColor = textColor, subTextColor = subTextColor) },
                        item2 = { MetricItem(label = "Oxygen Sat.", value = "$bciSpo2%", textColor = textColor, subTextColor = subTextColor) },
                        item3 = { MetricItem(label = "Skin Temp", value = "${String.format("%.1f", bciSkinTemp)}°C", textColor = textColor, subTextColor = subTextColor) },
                        item4 = { MetricItem(label = "GSR / Stress", value = "${String.format("%.2f", bciGsr)} µS", textColor = textColor, subTextColor = subTextColor) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Small scrollable signature text representation
                    Text(
                        text = "Φ⁵ Bell Lock: $bciSignature",
                        color = subTextColor,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }
        }

        // RIGHT COLUMN: 16-Channel Raw EEG Sparklines matrix
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1.8f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🧠 16-CHANNEL DRY ELECTRODE EEG",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Sampling Rate: 512 Hz | Impedance: <5kΩ",
                            color = subTextColor,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "STREAMING",
                            color = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(innerBg)
                        .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    val channels = listOf(
                        "Fp1", "Fp2", "F3", "F4", "C3", "C4", "P3", "P4",
                        "O1", "O2", "F7", "F8", "T3", "T4", "T5", "T6"
                    )

                    val infiniteTransition = rememberInfiniteTransition(label = "eeg_wave")
                    val phase by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 2f * Math.PI.toFloat(),
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "phase"
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(channels.size) { index ->
                            val chLabel = channels[index]
                            val eegValue = if (index < bciEegChannels.size) bciEegChannels[index] else 0.0f
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = String.format("%02d [%3s]", index + 1, chLabel),
                                    color = textColor.copy(alpha = 0.7f),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(55.dp)
                                )

                                // Live EEG Waveform sparkline Canvas
                                Canvas(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(cardBg.copy(alpha = 0.2f))
                                ) {
                                    val w = size.width
                                    val h = size.height
                                    val midY = h / 2f
                                    val pointsCount = 40
                                    val stepX = w / (pointsCount - 1)
                                    val path = Path()

                                    // Create waveform coordinates
                                    for (i in 0 until pointsCount) {
                                        val x = i * stepX
                                        // Dynamic oscillations based on sine wave, channel offsets, and the VM collected telemetry
                                        val angle = (i.toFloat() / pointsCount.toFloat()) * 4f * Math.PI.toFloat() + phase + (index * 0.4f)
                                        val amplitude = (h / 2.5f) * (0.6f + (eegValue / 80f).coerceIn(-0.4f, 0.4f))
                                        val y = midY + sin(angle) * amplitude

                                        if (i == 0) {
                                            path.moveTo(x, y)
                                        } else {
                                            path.lineTo(x, y)
                                        }
                                    }

                                    drawPath(
                                        path = path,
                                        color = accentColor.copy(alpha = 0.8f),
                                        style = Stroke(width = 1.5f)
                                    )
                                }

                                Text(
                                    text = String.format("%+5.1f uV", eegValue),
                                    color = schumannColor.copy(alpha = 0.9f),
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(50.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, textColor: Color, subTextColor: Color) {
    Column {
        Text(text = label, color = subTextColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun GridTwoByTwo(
    item1: @Composable () -> Unit,
    item2: @Composable () -> Unit,
    item3: @Composable () -> Unit,
    item4: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { item1() }
            Box(modifier = Modifier.weight(1f)) { item2() }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { item3() }
            Box(modifier = Modifier.weight(1f)) { item4() }
        }
    }
}

@Composable
fun SensorIndicator(label: String, value: String, barPct: Float, isLightTheme: Boolean, subTextColor: Color, textColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = subTextColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            Text(text = value, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        val clampedPct = barPct.coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { clampedPct },
            color = Color(0xFFFFAA00),
            trackColor = if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.08f),
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(1.dp))
        )
    }
}

@Composable
fun FaceScanBioDashboard(
    cardBg: Color,
    cardBorder: Color,
    accentColor: Color,
    textColor: Color,
    subTextColor: Color,
    innerBg: Color,
    schumannColor: Color,
    standardAccentColor: Color,
    faceBioQuality: Float,
    faceBioLivenessState: String,
    faceBioLivenessConfidence: Float,
    faceBioFusionScore: Float,
    faceBioDecision: String,
    faceBioIrisScore: Float,
    faceBioVoiceScore: Float,
    faceBioHeartRateScore: Float,
    faceBioSkinTextureScore: Float,
    faceBioVeinPatternScore: Float,
    faceBioPhi5Signature: String,
    faceBioAuditLogs: List<String>,
    faceBioEnrolledUsers: Int,
    isLightTheme: Boolean,
    viewModel: SovereignViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // LEFT COLUMN: 478-Point Face Scanner & Mesh View
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "👤 478-POINT 3D FACIAL LANDMARK SCANNER",
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "IR Depth Camera • 120 FPS • Liveness Verified",
                            color = subTextColor,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = faceBioLivenessState,
                            color = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas rendering 478-point mesh & laser scanner line
                val infiniteTransition = rememberInfiniteTransition(label = "face_scan")
                val scanLineY by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scan_line"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(innerBg)
                        .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val cx = w / 2f
                        val cy = h / 2f

                        // 1. Draw outer face oval contour
                        drawOval(
                            color = accentColor.copy(alpha = 0.35f),
                            topLeft = Offset(cx - w * 0.28f, cy - h * 0.38f),
                            size = androidx.compose.ui.geometry.Size(w * 0.56f, h * 0.76f),
                            style = Stroke(width = 1.8f)
                        )

                        // 2. Draw IR Depth concentric contour rings
                        for (r in 1..4) {
                            drawOval(
                                color = accentColor.copy(alpha = 0.08f * r),
                                topLeft = Offset(cx - (w * 0.06f * r), cy - (h * 0.08f * r)),
                                size = androidx.compose.ui.geometry.Size(w * 0.12f * r, h * 0.16f * r),
                                style = Stroke(width = 1f)
                            )
                        }

                        // 3. Draw Eyes & Iris pupils
                        val eyeY = cy - h * 0.12f
                        val leftEyeX = cx - w * 0.12f
                        val rightEyeX = cx + w * 0.12f
                        drawCircle(color = accentColor, center = Offset(leftEyeX, eyeY), radius = 8f)
                        drawCircle(color = accentColor, center = Offset(rightEyeX, eyeY), radius = 8f)
                        drawCircle(color = schumannColor, center = Offset(leftEyeX, eyeY), radius = 3f)
                        drawCircle(color = schumannColor, center = Offset(rightEyeX, eyeY), radius = 3f)

                        // Eyebrow arcs
                        drawLine(color = accentColor, start = Offset(leftEyeX - 20f, eyeY - 15f), end = Offset(leftEyeX + 20f, eyeY - 20f), strokeWidth = 2f)
                        drawLine(color = accentColor, start = Offset(rightEyeX - 20f, eyeY - 20f), end = Offset(rightEyeX + 20f, eyeY - 15f), strokeWidth = 2f)

                        // 4. Nose bridge & tip
                        val noseTopY = eyeY - 5f
                        val noseTipY = cy + h * 0.06f
                        drawLine(color = accentColor.copy(alpha = 0.8f), start = Offset(cx, noseTopY), end = Offset(cx, noseTipY), strokeWidth = 2f)
                        drawLine(color = accentColor.copy(alpha = 0.8f), start = Offset(cx - 12f, noseTipY + 8f), end = Offset(cx + 12f, noseTipY + 8f), strokeWidth = 1.5f)

                        // 5. Mouth contour
                        val mouthY = cy + h * 0.20f
                        drawLine(color = accentColor.copy(alpha = 0.8f), start = Offset(cx - 25f, mouthY), end = Offset(cx + 25f, mouthY), strokeWidth = 2f)

                        // 6. 478 Landmark Dots matrix grid
                        val gridRows = 16
                        val gridCols = 16
                        for (r in 0 until gridRows) {
                            for (c in 0 until gridCols) {
                                val px = cx - w * 0.35f + (c.toFloat() / (gridCols - 1)) * w * 0.70f
                                val py = cy - h * 0.40f + (r.toFloat() / (gridRows - 1)) * h * 0.80f
                                // Filter to approximate facial contour distance
                                val dx = (px - cx) / (w * 0.32f)
                                val dy = (py - cy) / (h * 0.42f)
                                if (dx * dx + dy * dy <= 1.0f) {
                                    drawCircle(color = accentColor.copy(alpha = 0.4f), center = Offset(px, py), radius = 1.5f)
                                }
                            }
                        }

                        // 7. Bounding Box Reticle
                        val bboxLeft = cx - w * 0.32f
                        val bboxTop = cy - h * 0.42f
                        val bboxRight = cx + w * 0.32f
                        val bboxBottom = cy + h * 0.42f
                        val cornerLen = 20f

                        // Corner brackets
                        drawLine(accentColor, Offset(bboxLeft, bboxTop), Offset(bboxLeft + cornerLen, bboxTop), 3f)
                        drawLine(accentColor, Offset(bboxLeft, bboxTop), Offset(bboxLeft, bboxTop + cornerLen), 3f)
                        drawLine(accentColor, Offset(bboxRight, bboxTop), Offset(bboxRight - cornerLen, bboxTop), 3f)
                        drawLine(accentColor, Offset(bboxRight, bboxTop), Offset(bboxRight, bboxTop + cornerLen), 3f)
                        drawLine(accentColor, Offset(bboxLeft, bboxBottom), Offset(bboxLeft + cornerLen, bboxBottom), 3f)
                        drawLine(accentColor, Offset(bboxLeft, bboxBottom), Offset(bboxLeft, bboxBottom - cornerLen), 3f)
                        drawLine(accentColor, Offset(bboxRight, bboxBottom), Offset(bboxRight - cornerLen, bboxBottom), 3f)
                        drawLine(accentColor, Offset(bboxRight, bboxBottom), Offset(bboxRight, bboxBottom - cornerLen), 3f)

                        // 8. Animated Vertical Laser Scanning Line
                        val lineY = h * scanLineY
                        drawLine(
                            color = schumannColor,
                            start = Offset(bboxLeft, lineY),
                            end = Offset(bboxRight, lineY),
                            strokeWidth = 2.5f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.triggerLivenessChallenge() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("TEST LIVENESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = { viewModel.triggerFaceScanSnapshot() },
                        colors = ButtonDefaults.buttonColors(containerColor = standardAccentColor, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("Φ⁵ SNAPSHOT", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // CENTER COLUMN: Multi-Modal Biometric Fusion Matrix
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🧬 MULTI-MODAL FUSION MATRIX",
                    color = standardAccentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Quantum State Weighted Decision Engine",
                    color = subTextColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    SensorIndicator("1. Face Geometry (35%, Φ⁺)", "${String.format("%.1f", faceBioQuality * 100)}%", faceBioQuality, isLightTheme, subTextColor, textColor)
                    SensorIndicator("2. Iris Code (25%, Φ⁻)", "${String.format("%.1f", faceBioIrisScore * 100)}%", faceBioIrisScore, isLightTheme, subTextColor, textColor)
                    SensorIndicator("3. Voice Print (15%, Ψ⁺)", "${String.format("%.1f", faceBioVoiceScore * 100)}%", faceBioVoiceScore, isLightTheme, subTextColor, textColor)
                    SensorIndicator("4. Heart Rate rPPG (10%, Ψ⁻)", "${String.format("%.1f", faceBioHeartRateScore * 100)}%", faceBioHeartRateScore, isLightTheme, subTextColor, textColor)
                    SensorIndicator("5. Skin Texture (10%, Φ⁺)", "${String.format("%.1f", faceBioSkinTextureScore * 100)}%", faceBioSkinTextureScore, isLightTheme, subTextColor, textColor)
                    SensorIndicator("6. Subdermal Vein (5%, Φ⁻)", "${String.format("%.1f", faceBioVeinPatternScore * 100)}%", faceBioVeinPatternScore, isLightTheme, subTextColor, textColor)

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(innerBg)
                            .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Fused Score", color = subTextColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("${String.format("%.1f", faceBioFusionScore * 100)}%", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Decision", color = subTextColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(faceBioDecision, color = schumannColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Enrolled Profiles", color = subTextColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("$faceBioEnrolledUsers Users", color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.enrollFaceBioUser("operator") },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("ENROLL USER", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = { viewModel.verifyFaceBioUser("operator") },
                        colors = ButtonDefaults.buttonColors(containerColor = standardAccentColor, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("VERIFY USER", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // RIGHT COLUMN: Phi-5 (Φ⁵) Quantum Identity & Audit Logs
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔮 Φ⁵ BELL PAIR QUANTUM IDENTITY",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Post-Quantum AES-256 + Kyber-1024",
                    color = subTextColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(innerBg)
                        .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Active Bell Pairs: Φ⁺ (Face) • Φ⁻ (Iris) • Ψ⁺ (Voice) • Ψ⁻ (rPPG)", color = schumannColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("CHSH Test: S = 2.82 > 2.0 (Bell Inequality Violated)", color = accentColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("Quantum Signature: $faceBioPhi5Signature", color = textColor.copy(alpha = 0.85f), fontSize = 8.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "📜 SECURITY AUDIT STREAM",
                    color = subTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(innerBg)
                        .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(faceBioAuditLogs) { log ->
                            Text(
                                text = log,
                                color = schumannColor.copy(alpha = 0.9f),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
