package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SovereignData
import com.example.ui.SovereignViewModel

// Custom Syntax Highlighting engine built right into Jetpack Compose
class CodeHighlightTransformation(private val language: String, private val isLight: Boolean) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val annotated = buildAnnotatedString {
            val content = text.text
            append(content)
            
            val keywordColor = if (isLight) Color(0xFF7C1153) else Color(0xFFFF5F9E)
            val stringColor = if (isLight) Color(0xFF1B6A27) else Color(0xFF00FF88)
            val commentColor = if (isLight) Color(0xFF6A737D) else Color(0xFF8AA4BD).copy(alpha = 0.6f)
            val numberColor = if (isLight) Color(0xFF005CC5) else Color(0xFFFFAA00)
            
            // Apply single line comments `#` or `//`
            val commentRegex = Regex("#.*|//.*")
            commentRegex.findAll(content).forEach { match ->
                addStyle(SpanStyle(color = commentColor), match.range.first, match.range.last + 1)
            }
            
            // Map typical language keywords
            val keywords = when (language) {
                "YAML" -> listOf("schumann_lock_target", "max_peers", "network", "system", "dns", "nodes", "target", "peers")
                "Shell" -> listOf("sudo", "su", "useradd", "passwd", "chmod", "pkg", "install", "remove", "update", "clear", "help", "whoami")
                "JavaScript" -> listOf("const", "let", "var", "function", "return", "if", "else", "for", "while", "class", "import", "export")
                "HTML" -> listOf("html", "head", "body", "div", "span", "p", "a", "h1", "h2", "h3", "script", "style")
                else -> emptyList()
            }
            
            keywords.forEach { word ->
                val wordRegex = Regex("\\b$word\\b")
                wordRegex.findAll(content).forEach { match ->
                    addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
                }
            }
            
            // String literals
            val stringRegex = Regex("\".*?\"|'.*?'")
            stringRegex.findAll(content).forEach { match ->
                addStyle(SpanStyle(color = stringColor), match.range.first, match.range.last + 1)
            }

            // Real-time digit/number identification
            val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
            numberRegex.findAll(content).forEach { match ->
                addStyle(SpanStyle(color = numberColor), match.range.first, match.range.last + 1)
            }
        }
        return TransformedText(annotated, OffsetMapping.Identity)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesystemScreen(
    viewModel: SovereignViewModel,
    modifier: Modifier = Modifier
) {
    val files by viewModel.files.collectAsState()
    val selectedFilePath by viewModel.selectedFilePath.collectAsState()
    val selectedFileContent by viewModel.selectedFileContent.collectAsState()
    val isLightTheme by viewModel.isLightTheme.collectAsState()
    val packages by viewModel.packages.collectAsState()

    // Determine if Advanced Code Editor package is active
    val isEditorInstalled = remember(packages) {
        packages.find { it.name == "sovereign-editor" }?.isInstalled == true
    }

    var editContent by remember(selectedFilePath) { mutableStateOf(selectedFileContent) }

    // Synchronize content updates from VM state
    LaunchedEffect(selectedFileContent) {
        editContent = selectedFileContent
    }

    // Color schema adaptive to Light & Dark options
    val textColor = if (isLightTheme) Color(0xFF1E293B) else Color(0xFFE8E5DF)
    val subTextColor = if (isLightTheme) Color(0xFF475569) else Color(0xFF8AA4BD)
    val cardBg = if (isLightTheme) Color.White else Color.White.copy(alpha = 0.05f)
    val cardBorder = if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f)
    val innerBg = if (isLightTheme) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.35f)
    val accentColor = if (isLightTheme) Color(0xFF007799) else Color(0xFF00D9FF)

    // Language identification based on file extension
    val detectedLanguage = remember(selectedFilePath) {
        val path = selectedFilePath ?: ""
        when {
            path.endsWith(".yaml") || path.endsWith(".yml") -> "YAML"
            path.endsWith(".sh") || path.endsWith(".bash") -> "Shell"
            path.endsWith(".js") || path.endsWith(".json") -> "JavaScript"
            path.endsWith(".html") || path.endsWith(".css") -> "HTML"
            else -> "Plain Text"
        }
    }

    // Find & Replace form state
    var findText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }
    val matchCount = remember(editContent, findText) {
        if (findText.isEmpty()) 0
        else Regex(Regex.escape(findText)).findAll(editContent).count()
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // LEFT PANE: ext4 Directory Explorer
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📂 SOVEREIGN FILESYSTEM (ext4)",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(innerBg)
                        .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(SovereignData.directoryStructure) { filePath ->
                            val isSelected = filePath == selectedFilePath
                            val isBinary = filePath.endsWith(".EFI") || filePath.endsWith(".img") || filePath.endsWith(".db")
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) (if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.08f)) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) accentColor.copy(alpha = 0.4f) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.updateSelectedFile(filePath)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBinary) Icons.Default.FolderZip else Icons.Default.Description,
                                    contentDescription = "File Type",
                                    tint = if (isBinary) Color(0xFFFF5555) else Color(0xFF00FF88),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = filePath,
                                    color = if (isSelected) textColor else subTextColor,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // RIGHT PANE: Modern Syntax highlighting IDE Editor
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1.6f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with File Name, Language Mode & Status Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "✍️ EDITOR: ${selectedFilePath ?: "no file"}",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "Lang: $detectedLanguage",
                                color = subTextColor,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isEditorInstalled) Color(0x2000FF88) else Color(0x20FFAA00))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (isEditorInstalled) "ADVANCED SYNTAX HIGHLIGHTING ACTIVE" else "STANDARD PLAIN-TEXT MODE",
                                    color = if (isEditorInstalled) Color(0xFF00FF88) else Color(0xFFFFAA00),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    if (selectedFilePath != null && !selectedFilePath!!.run { endsWith(".EFI") || endsWith(".img") || endsWith(".db") }) {
                        IconButton(
                            onClick = { viewModel.saveSelectedFile(editContent) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("save_file_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save file",
                                tint = Color(0xFF00FF88),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val isBinary = selectedFilePath?.run { endsWith(".EFI") || endsWith(".img") || endsWith(".db") } ?: false

                if (isBinary) {
                    // HEX DUMPER VISUALIZER
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(innerBg)
                            .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        val hexDump = remember(selectedFilePath) {
                            buildString {
                                appendLine("HEX DUMP ENGINE • SYSTEM BINARY FILE VISUALIZER")
                                appendLine("==============================================")
                                for (i in 0 until 14) {
                                    val offset = String.format("%08X", i * 16)
                                    val bytes = List(16) { String.format("%02X", (0..255).random()) }.joinToString(" ")
                                    val ascii = List(16) { ('.'.code).toChar() }.joinToString("")
                                    appendLine("$offset  $bytes  |$ascii|")
                                }
                            }
                        }
                        Text(
                            text = hexDump,
                            color = Color(0xFFFF5555).copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                } else {
                    // Find & Replace panel (Visible only if sovereign-editor package is installed!)
                    if (isEditorInstalled) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = innerBg),
                            border = BorderStroke(1.dp, cardBorder.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = findText,
                                    onValueChange = { findText = it },
                                    placeholder = { Text("Find string...", fontSize = 9.sp) },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp, fontFamily = FontFamily.Monospace),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = cardBg,
                                        unfocusedContainerColor = cardBg
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("editor_find_input")
                                )

                                OutlinedTextField(
                                    value = replaceText,
                                    onValueChange = { replaceText = it },
                                    placeholder = { Text("Replace with...", fontSize = 9.sp) },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp, fontFamily = FontFamily.Monospace),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = cardBg,
                                        unfocusedContainerColor = cardBg
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("editor_replace_input")
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = "$matchCount matches",
                                        color = if (matchCount > 0) Color(0xFF00FF88) else subTextColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Button(
                                        onClick = {
                                            if (findText.isNotEmpty()) {
                                                editContent = editContent.replaceFirst(findText, replaceText)
                                            }
                                        },
                                        enabled = matchCount > 0,
                                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("REPLACE", color = if (isLightTheme) Color.White else Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }

                                    Button(
                                        onClick = {
                                            if (findText.isNotEmpty()) {
                                                editContent = editContent.replace(findText, replaceText)
                                            }
                                        },
                                        enabled = matchCount > 0,
                                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("ALL", color = if (isLightTheme) Color.White else Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }

                    // MAIN TEXT FIELD WITH OPTIONAL SYNTAX HIGHLIGHTING & LINE NUMBERS
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(innerBg)
                            .border(1.dp, cardBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            // Virtual Line numbers
                            val lineCount = editContent.lines().size
                            val lineNumbersText = remember(lineCount) {
                                (1..lineCount).joinToString("\n") { String.format("%02d", it) }
                            }

                            Text(
                                text = lineNumbersText,
                                color = subTextColor.copy(alpha = 0.4f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp,
                                modifier = Modifier
                                    .width(32.dp)
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                textAlign = TextAlign.End
                            )

                            VerticalDivider(color = cardBorder.copy(alpha = 0.3f))

                            // Actual text area input
                            OutlinedTextField(
                                value = editContent,
                                onValueChange = { editContent = it },
                                textStyle = LocalTextStyle.current.copy(
                                    color = textColor,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                ),
                                visualTransformation = if (isEditorInstalled) {
                                    CodeHighlightTransformation(detectedLanguage, isLightTheme)
                                } else {
                                    VisualTransformation.None
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .testTag("code_editor_field")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.saveSelectedFile(editContent) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (isLightTheme) listOf(Color(0xFF005CC5), Color(0xFF007799)) else listOf(Color(0xFFFFAA00), Color(0xFFFF5555))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = "💾 SAVE CONFIGURATION & RECALIBRATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isLightTheme) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}
