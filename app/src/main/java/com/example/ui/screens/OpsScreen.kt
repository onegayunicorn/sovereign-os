package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SovereignViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpsScreen(
    viewModel: SovereignViewModel,
    modifier: Modifier = Modifier
) {
    val isBuildingIso by viewModel.isBuildingIso.collectAsState()
    val isoBuildProgress by viewModel.isoBuildProgress.collectAsState()
    val isoBuildLogs by viewModel.isoBuildLogs.collectAsState()

    val isSelfTerminating by viewModel.isSelfTerminating.collectAsState()
    val selfTerminateLogs by viewModel.terminateLogs.collectAsState()

    val dnaSequence by viewModel.dnaSequence.collectAsState()
    var rawDnaInput by remember { mutableStateOf(dnaSequence) }

    val terminalLogs by viewModel.terminalLogs.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var terminalInputLine by remember { mutableStateOf("") }
    val isLightTheme by viewModel.isLightTheme.collectAsState()

    // Coroutines for auto-scrolling terminal logs
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to bottom whenever terminal logs are added
    LaunchedEffect(terminalLogs.size) {
        if (terminalLogs.isNotEmpty()) {
            listState.animateScrollToItem(terminalLogs.size - 1)
        }
    }

    // Color theme variables based on Light / Dark Mode selection
    val textColor = if (isLightTheme) Color(0xFF1E293B) else Color(0xFFE8E5DF)
    val subTextColor = if (isLightTheme) Color(0xFF475569) else Color(0xFF8AA4BD)
    val cardBg = if (isLightTheme) Color.White else Color.White.copy(alpha = 0.05f)
    val cardBorder = if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f)
    val innerBg = if (isLightTheme) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.35f)
    val accentColor = if (isLightTheme) Color(0xFF007799) else Color(0xFF00D9FF)
    val termPromptColor = if (isLightTheme) Color(0xFF007799) else Color(0xFF00FF88)
    val termLogColor = if (isLightTheme) Color(0xFF1E293B) else Color(0xFF00FF88)

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // LEFT COLUMN: Reproducible Compiler & DNA Alignment
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // REPRODUCIBLE SYSTEM COMPILER
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
                            text = "🛠️ REPRODUCIBLE ISO BUILDER",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        if (isBuildingIso) {
                            CircularProgressIndicator(
                                color = accentColor,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
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
                            .padding(12.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (isoBuildLogs.isEmpty()) {
                                item {
                                    Text(
                                        text = "System compilation idling. Ready to execute build_iso.sh parameters.",
                                        color = subTextColor.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                items(isoBuildLogs) { log ->
                                    Text(
                                        text = log,
                                        color = if (log.contains("SUCCESSFUL")) Color(0xFF00FF88) else subTextColor,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isBuildingIso) {
                        LinearProgressIndicator(
                            progress = { isoBuildProgress },
                            color = accentColor,
                            trackColor = if (isLightTheme) Color(0xFFCBD5E1) else Color.White.copy(alpha = 0.08f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = { viewModel.triggerBuildIso() },
                        enabled = !isBuildingIso,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (isLightTheme) listOf(Color(0xFF005CC5), Color(0xFF007799)) else listOf(Color(0xFF00D9FF), Color(0xFFFF00FF))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = "TRIGGER REPRODUCIBLE SYSTEM COMPILATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isLightTheme) Color.White else Color.Black
                        )
                    }
                }
            }

            // DNA BLOCH COORDINATE PAYLOAD MAPPING
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🧬 DNA BLOCH ALIGNMENT SYSTEM",
                        color = if (isLightTheme) Color(0xFF1B6A27) else Color(0xFF00FF88),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Inject arbitrary DNA strands (bases A, T, C, G) to orient state vectors of the local quantum encryption core:",
                        color = subTextColor,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = rawDnaInput,
                        onValueChange = { input ->
                            val cleanInput = input.uppercase().filter { it in "ATCG" }
                            rawDnaInput = cleanInput
                            viewModel.updateDnaSequence(cleanInput)
                        },
                        label = { Text("DNA Chain Payload", fontSize = 11.sp, color = accentColor) },
                        textStyle = LocalTextStyle.current.copy(
                            color = textColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            letterSpacing = 2.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = innerBg,
                            unfocusedContainerColor = innerBg,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = cardBorder.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dna_sequence_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vector Length: ${dnaSequence.length} bases mapped.",
                        color = if (isLightTheme) Color(0xFF166534) else Color(0xFF00FF88).copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // RIGHT COLUMN: CLI Terminal Shell (Top) & Self-Terminate (Bottom)
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // SOVEREIGN OS CLI INTERACTIVE TERMINAL
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Terminal Icon",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "💻 SOVEREIGN OS TERMCRAFT SH",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Shell Console log screen
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isLightTheme) Color(0xFFF8FAFC) else Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(10.dp)
                    ) {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(terminalLogs) { log ->
                                Text(
                                    text = log,
                                    color = if (log.contains("Authentication failure") || log.contains("Permission denied") || log.contains("not found")) Color(0xFFFF5555) else termLogColor,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Line
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentUser.username}@sovereign:~$",
                            color = termPromptColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = terminalInputLine,
                            onValueChange = { terminalInputLine = it },
                            placeholder = { Text("type 'help'...", fontSize = 10.sp, color = subTextColor.copy(alpha = 0.5f)) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = textColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = innerBg,
                                unfocusedContainerColor = innerBg,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = cardBorder.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("terminal_input_line")
                        )

                        Button(
                            onClick = {
                                if (terminalInputLine.isNotBlank()) {
                                    viewModel.executeTerminalCommand(terminalInputLine)
                                    terminalInputLine = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                "EXEC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isLightTheme) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            // NUCLEAR OPTION SELF TERMINATOR
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1.1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "💀 COHERENCE COLLAPSE PROTOCOL",
                            color = Color(0xFFFF5555),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Initiates cryptographic system shredding.",
                            color = subTextColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        AnimatedVisibility(visible = isSelfTerminating) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(innerBg)
                                    .border(1.dp, Color(0xFFFF5555).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            ) {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    items(selfTerminateLogs) { log ->
                                        Text(
                                            text = log,
                                            color = Color(0xFFFF5555),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.triggerSelfTerminate() },
                        enabled = !isSelfTerminating,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3333)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("self_terminate_button")
                    ) {
                        Text(
                            text = "DESTROY LOCAL INSTANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
