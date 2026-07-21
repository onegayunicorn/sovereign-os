package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.ui.SovereignTab
import com.example.ui.SovereignViewModel
import com.example.ui.components.BlochSphereView
import com.example.ui.screens.BootScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FilesystemScreen
import com.example.ui.screens.OpsScreen
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SovereignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isLightTheme by viewModel.isLightTheme.collectAsState()
            MyApplicationTheme(darkTheme = !isLightTheme, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isLightTheme) Color(0xFFF8FAFC) else Color(0xFF02040A)
                ) {
                    SovereignAppShell(viewModel)
                }
            }
        }
    }
}

@Composable
fun SovereignAppShell(
    viewModel: SovereignViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isBooted by viewModel.isBooted.collectAsState()
    val coherence by viewModel.coherence.collectAsState()
    val crystalFreq by viewModel.timeCrystalFrequency.collectAsState()
    val dnaSequence by viewModel.dnaSequence.collectAsState()
    val isLightTheme by viewModel.isLightTheme.collectAsState()

    // Interactive Theme styling
    val systemBgColor = if (isLightTheme) Color(0xFFF1F5F9) else Color(0xFF02040A)
    val glowColor1 = if (isLightTheme) Color(0xFF38BDF8).copy(alpha = 0.2f) else Color(0xFF00D9FF).copy(alpha = 0.12f)
    val glowColor2 = if (isLightTheme) Color(0xFFF472B6).copy(alpha = 0.2f) else Color(0xFFFF00FF).copy(alpha = 0.12f)

    val barBgColor = if (isLightTheme) Color.Black.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.04f)
    val barTextColor = if (isLightTheme) Color(0xFF1E293B) else Color(0xFFE8E5DF)
    val barSubTextColor = if (isLightTheme) Color(0xFF475569) else Color(0xFF8AA4BD).copy(alpha = 0.7f)
    val barBorderColor = if (isLightTheme) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.08f)

    val accentColor = if (isLightTheme) Color(0xFF007799) else Color(0xFF00D9FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(systemBgColor)
                val cyanGlow = Brush.radialGradient(
                    colors = listOf(glowColor1, Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.75f),
                    radius = size.width * 0.7f
                )
                drawRect(brush = cyanGlow)
                val magentaGlow = Brush.radialGradient(
                    colors = listOf(glowColor2, Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.25f),
                    radius = size.width * 0.7f
                )
                drawRect(brush = magentaGlow)
            }
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // TOP GLOBAL BAR: Sovereign System Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(barBgColor)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SOVEREIGN ECOSYSTEM Ω",
                    color = barTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "V1.0.0-OMEGA // Target: Moto G35 (ARM64)",
                    color = barSubTextColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Central quick telemetry status chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time crystal frequency readout
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLightTheme) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.06f))
                        .border(1.dp, barBorderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "💎 LOCK: ${String.format("%.3f", crystalFreq)} Hz",
                        color = if (isLightTheme) Color(0xFFC026D3) else Color(0xFFFF00FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Coherence index readout
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLightTheme) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.06f))
                        .border(1.dp, barBorderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Φ: ${String.format("%.4f", coherence)}",
                        color = if (coherence > 0.98f) (if (isLightTheme) Color(0xFF166534) else Color(0xFF00FF88)) else Color(0xFFFFAA00),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Boot state Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isBooted) (if (isLightTheme) Color(0xFFDCFCE7) else Color(0x2800FF88)) else Color(0x28FF5555))
                        .border(1.dp, if (isBooted) (if (isLightTheme) Color(0xFF166534) else Color(0xFF00FF88).copy(alpha = 0.3f)) else Color(0xFFFF5555).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isBooted) "SYSTEM ONLINE" else "SYSTEM HALTED",
                        color = if (isBooted) (if (isLightTheme) Color(0xFF166534) else Color(0xFF00FF88)) else Color(0xFFFF5555),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = barBorderColor)

        // CONTENT REGION
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (!isBooted) {
                BootScreen(viewModel = viewModel)
            } else {
                when (currentTab) {
                    SovereignTab.BOOT -> BootScreen(viewModel = viewModel)
                    SovereignTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    SovereignTab.FILESYSTEM -> FilesystemScreen(viewModel = viewModel)
                    SovereignTab.OPERATIONS -> {
                        // Double panel pane: Bloch Sphere rendering (left) and System operations (right)
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Render DNA Bloch Sphere view on the left pane
                            Card(
                                colors = CardDefaults.cardColors(containerColor = barBgColor),
                                border = BorderStroke(1.dp, barBorderColor),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "🔮 QUANTUM STATE BLOCH SPHERE",
                                        color = accentColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Active Payload: $dnaSequence (Swipe to rotate 3D view)",
                                        color = barSubTextColor,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isLightTheme) Color.White else Color.Black.copy(alpha = 0.3f))
                                            .border(1.dp, barBorderColor, RoundedCornerShape(16.dp))
                                    ) {
                                        BlochSphereView(dnaSequence = dnaSequence)
                                    }
                                }
                            }

                            // Render operations configurations on the right pane
                            Box(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .fillMaxHeight()
                            ) {
                                OpsScreen(viewModel = viewModel)
                            }
                        }
                    }
                    SovereignTab.BROWSER -> BrowserScreen(viewModel = viewModel)
                    SovereignTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }

        // BOTTOM SYSTEM NAV BAR
        if (isBooted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isLightTheme) Color.White else Color.Black.copy(alpha = 0.35f))
                    .border(1.dp, barBorderColor, RoundedCornerShape(0.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabButton(
                        label = "TELEMETRY",
                        isSelected = currentTab == SovereignTab.DASHBOARD,
                        onClick = { viewModel.selectTab(SovereignTab.DASHBOARD) },
                        testTag = "tab_dashboard",
                        isLightTheme = isLightTheme
                    )
                    TabButton(
                        label = "FILESYSTEM",
                        isSelected = currentTab == SovereignTab.FILESYSTEM,
                        onClick = { viewModel.selectTab(SovereignTab.FILESYSTEM) },
                        testTag = "tab_filesystem",
                        isLightTheme = isLightTheme
                    )
                    TabButton(
                        label = "CORE & OPS",
                        isSelected = currentTab == SovereignTab.OPERATIONS,
                        onClick = { viewModel.selectTab(SovereignTab.OPERATIONS) },
                        testTag = "tab_operations",
                        isLightTheme = isLightTheme
                    )
                    TabButton(
                        label = "BROWSER",
                        isSelected = currentTab == SovereignTab.BROWSER,
                        onClick = { viewModel.selectTab(SovereignTab.BROWSER) },
                        testTag = "tab_browser",
                        isLightTheme = isLightTheme
                    )
                    TabButton(
                        label = "SETTINGS",
                        isSelected = currentTab == SovereignTab.SETTINGS,
                        onClick = { viewModel.selectTab(SovereignTab.SETTINGS) },
                        testTag = "tab_settings",
                        isLightTheme = isLightTheme
                    )
                }
            }
        }
    }
}

@Composable
fun TabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    isLightTheme: Boolean
) {
    val selectedBg = if (isLightTheme) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.08f)
    val selectedBorder = if (isLightTheme) Color(0xFF007799).copy(alpha = 0.4f) else Color(0xFF00D9FF).copy(alpha = 0.4f)
    val selectedText = if (isLightTheme) Color(0xFF007799) else Color(0xFF00D9FF)
    val unselectedText = if (isLightTheme) Color(0xFF475569) else Color(0xFF8AA4BD)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) selectedBg else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) selectedBorder else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) selectedText else unselectedText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
    }
}
