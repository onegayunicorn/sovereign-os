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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SovereignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SovereignViewModel,
    modifier: Modifier = Modifier
) {
    val isLightTheme by viewModel.isLightTheme.collectAsState()
    val users by viewModel.users.collectAsState()
    val packages by viewModel.packages.collectAsState()

    // Color definitions based on Light / Dark mode
    val textColor = if (isLightTheme) Color(0xFF1E293B) else Color(0xFFE8E5DF)
    val subTextColor = if (isLightTheme) Color(0xFF475569) else Color(0xFF8AA4BD)
    val cardBg = if (isLightTheme) Color.White else Color.White.copy(alpha = 0.05f)
    val cardBorder = if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.15f)
    val innerBg = if (isLightTheme) Color(0xFFF1F5F9) else Color.Black.copy(alpha = 0.35f)
    val accentColor = if (isLightTheme) Color(0xFF007799) else Color(0xFF00D9FF)

    // Form States
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var makeSudoer by remember { mutableStateOf(false) }
    var permRead by remember { mutableStateOf(true) }
    var permWrite by remember { mutableStateOf(true) }
    var permExec by remember { mutableStateOf(true) }

    var pkgSearchQuery by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // LEFT PANE: SYSTEM CONFIGS & ACCOUNT CREATION
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // SYSTEM THEME SWITCHER
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎨 APPEARANCE & THEME",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isLightTheme) "Light Theme Active" else "Dark Theme Active",
                                color = textColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Toggle system theme variables dynamically.",
                                color = subTextColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleTheme() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLightTheme) Color(0xFF475569) else Color(0xFF00D9FF)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isLightTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Theme Icon",
                                tint = if (isLightTheme) Color.White else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isLightTheme) "DARK MODE" else "LIGHT MODE",
                                color = if (isLightTheme) Color.White else Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // USER CREATION FORM
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "👤 CREATE USER ACCOUNT",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        label = { Text("Username", fontSize = 10.sp) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = innerBg,
                            unfocusedContainerColor = innerBg,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = cardBorder.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .testTag("new_user_input")
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Password", fontSize = 10.sp) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = innerBg,
                            unfocusedContainerColor = innerBg,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = cardBorder.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .testTag("new_pass_input")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = makeSudoer,
                            onCheckedChange = { makeSudoer = it },
                            colors = CheckboxDefaults.colors(checkedColor = accentColor)
                        )
                        Text(
                            text = "Grant Administrator (sudo) privileges",
                            color = textColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Home Folder Permissions:",
                        color = subTextColor,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = permRead, onCheckedChange = { permRead = it }, colors = CheckboxDefaults.colors(checkedColor = accentColor))
                            Text("Read (r)", color = textColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = permWrite, onCheckedChange = { permWrite = it }, colors = CheckboxDefaults.colors(checkedColor = accentColor))
                            Text("Write (w)", color = textColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = permExec, onCheckedChange = { permExec = it }, colors = CheckboxDefaults.colors(checkedColor = accentColor))
                            Text("Exec (x)", color = textColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (newUsername.isNotBlank() && newPassword.isNotBlank()) {
                                val r = if (permRead) "r" else "-"
                                val w = if (permWrite) "w" else "-"
                                val x = if (permExec) "x" else "-"
                                viewModel.addUser(newUsername, newPassword, makeSudoer, "$r$w$x")
                                newUsername = ""
                                newPassword = ""
                                makeSudoer = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("add_user_button")
                    ) {
                        Text(
                            text = "CREATE ACCOUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isLightTheme) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        // MIDDLE PANE: USER LIST & DIRECTORY PERMISSIONS
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
                    text = "👥 ACTIVE SYSTEM ACCOUNTS",
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(innerBg)
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users) { u ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isLightTheme) Color.White else Color.White.copy(alpha = 0.04f))
                                    .border(1.dp, if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = u.username.uppercase(),
                                        color = textColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (u.isSudoer) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0x20FFAA00))
                                                .border(1.dp, Color(0xFFFFAA00).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "SUDOER",
                                                color = Color(0xFFFFAA00),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Home: /${u.homeDir}",
                                    color = subTextColor,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Folder Permissions: ${u.permissions}",
                                    color = if (u.permissions.contains("w")) Color(0xFF00FF88) else Color(0xFFFF5555),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // RIGHT PANE: INTEGRATED PACKAGE MANAGER (SOFTWARE CENTER)
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📦 REPOSITORY SOFTWARE CENTER",
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = pkgSearchQuery,
                    onValueChange = { pkgSearchQuery = it },
                    placeholder = { Text("Search packages...", fontSize = 10.sp) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp)) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = innerBg,
                        unfocusedContainerColor = innerBg,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = cardBorder.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                val filteredPackages = remember(packages, pkgSearchQuery) {
                    if (pkgSearchQuery.isBlank()) packages
                    else packages.filter { it.name.contains(pkgSearchQuery, ignoreCase = true) || it.description.contains(pkgSearchQuery, ignoreCase = true) }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(innerBg)
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredPackages) { pkg ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isLightTheme) Color.White else Color.White.copy(alpha = 0.04f))
                                    .border(1.dp, if (isLightTheme) Color(0xFFE2E8F0) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = pkg.name,
                                            color = textColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "${pkg.version} • ${pkg.sizeKb} KB",
                                            color = subTextColor,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    if (pkg.isInstalled) {
                                        Button(
                                            onClick = { viewModel.uninstallPackage(pkg.name) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x30FF5555)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("REMOVE", color = Color(0xFFFF5555), fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.installPackage(pkg.name) },
                                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("INSTALL", color = if (isLightTheme) Color.White else Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = pkg.description,
                                    color = subTextColor,
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
