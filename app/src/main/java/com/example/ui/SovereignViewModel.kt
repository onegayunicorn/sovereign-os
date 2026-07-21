package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SovereignData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- User Management Models ---
data class SovereignUser(
    val username: String,
    val passwordHash: String,
    val isSudoer: Boolean = false,
    val homeDir: String,
    var permissions: String = "rwx" // r, w, x (e.g. "rwx", "r-x", "r--")
)

// --- Package Manager Models ---
data class SovereignPackage(
    val name: String,
    val version: String,
    val description: String,
    val sizeKb: Int,
    val category: String,
    val isInstalled: Boolean = false
)

enum class SovereignTab {
    BOOT, DASHBOARD, FILESYSTEM, OPERATIONS, BROWSER, SETTINGS
}

class SovereignViewModel : ViewModel() {

    // Current navigation tab
    private val _currentTab = MutableStateFlow(SovereignTab.BOOT)
    val currentTab: StateFlow<SovereignTab> = _currentTab.asStateFlow()

    // --- Theme Switcher State ---
    private val _isLightTheme = MutableStateFlow(false)
    val isLightTheme: StateFlow<Boolean> = _isLightTheme.asStateFlow()

    // --- User Management State ---
    private val _users = MutableStateFlow<List<SovereignUser>>(listOf(
        SovereignUser("operator", "admin123", true, "home/operator", "rwx"),
        SovereignUser("guest", "guest123", false, "home/guest", "r-x")
    ))
    val users: StateFlow<List<SovereignUser>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<SovereignUser>(_users.value.first())
    val currentUser: StateFlow<SovereignUser> = _currentUser.asStateFlow()

    // --- Package Manager State ---
    private val _packages = MutableStateFlow<List<SovereignPackage>>(listOf(
        SovereignPackage("sovereign-editor", "v1.2.0", "Advanced file editor with syntax highlighting & find-replace.", 340, "Development", false),
        SovereignPackage("sovereign-browser", "v2.1.4", "Offline sandbox environment web browser application.", 890, "Utility", false),
        SovereignPackage("time-stabilizer", "v1.0.1", "Quantum calibration daemon for Schumann phase locking.", 120, "System", true),
        SovereignPackage("mesh-optimizer", "v0.8.5", "Kademlia DHT routing matrix debugger utility.", 180, "Network", false),
        SovereignPackage("samsung-a17-bci", "v3.2.1", "Quantum temporal BCI subsystem integration for SM-A176B.", 1520, "Biometrics", false)
    ))
    val packages: StateFlow<List<SovereignPackage>> = _packages.asStateFlow()

    // --- Terminal / Command Line Interface State ---
    private val _terminalLogs = MutableStateFlow<List<String>>(listOf(
        "Welcome to Sovereign OS Terminal v1.0.0-omega",
        "System is running on secure kernel build 6.8-sovereign-hardened",
        "Type 'help' to view the list of available commands.",
        "Active Session: operator@sovereign",
        ""
    ))
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    private val _isSudoActive = MutableStateFlow(false)
    val isSudoActive: StateFlow<Boolean> = _isSudoActive.asStateFlow()

    // --- Boot Sequence State ---
    private val _isBooted = MutableStateFlow(false)
    val isBooted: StateFlow<Boolean> = _isBooted.asStateFlow()

    private val _isBooting = MutableStateFlow(false)
    val isBooting: StateFlow<Boolean> = _isBooting.asStateFlow()

    private val _bootProgress = MutableStateFlow(0f)
    val bootProgress: StateFlow<Float> = _bootProgress.asStateFlow()

    private val _currentBootingStage = MutableStateFlow("")
    val currentBootingStage: StateFlow<String> = _currentBootingStage.asStateFlow()

    private val _bootLogs = MutableStateFlow<List<String>>(emptyList())
    val bootLogs: StateFlow<List<String>> = _bootLogs.asStateFlow()

    // --- Filesystem State ---
    private val _files = MutableStateFlow<Map<String, String>>(SovereignData.defaultFiles)
    val files: StateFlow<Map<String, String>> = _files.asStateFlow()

    private val _selectedFilePath = MutableStateFlow<String?>("README.txt")
    val selectedFilePath: StateFlow<String?> = _selectedFilePath.asStateFlow()

    private val _selectedFileContent = MutableStateFlow("")
    val selectedFileContent: StateFlow<String> = _selectedFileContent.asStateFlow()

    // --- Live Metrics (Dashboard) State ---
    private val _coherence = MutableStateFlow(0.947f)
    val coherence: StateFlow<Float> = _coherence.asStateFlow()

    private val _timeCrystalFrequency = MutableStateFlow(7.830)
    val timeCrystalFrequency: StateFlow<Double> = _timeCrystalFrequency.asStateFlow()

    private val _timeCrystalRadius = MutableStateFlow(1.000000)
    val timeCrystalRadius: StateFlow<Double> = _timeCrystalRadius.asStateFlow()

    private val _schumannLock = MutableStateFlow(true)
    val schumannLock: StateFlow<Boolean> = _schumannLock.asStateFlow()

    private val _peerCount = MutableStateFlow(64)
    val peerCount: StateFlow<Int> = _peerCount.asStateFlow()

    private val _consensusCount = MutableStateFlow(43)
    val consensusCount: StateFlow<Int> = _consensusCount.asStateFlow()

    private val _barometerHpa = MutableStateFlow(1013.25f)
    val barometerHpa: StateFlow<Float> = _barometerHpa.asStateFlow()

    private val _bciAttention = MutableStateFlow(0.87f)
    val bciAttention: StateFlow<Float> = _bciAttention.asStateFlow()

    private val _npuLoad = MutableStateFlow(34)
    val npuLoad: StateFlow<Int> = _npuLoad.asStateFlow()

    private val _coherenceHistory = MutableStateFlow<List<Float>>(List(30) { 0.947f })
    val coherenceHistory: StateFlow<List<Float>> = _coherenceHistory.asStateFlow()

    private val _consoleLogs = MutableStateFlow<List<String>>(listOf("System initialised and idling."))
    val consoleLogs: StateFlow<List<String>> = _consoleLogs.asStateFlow()

    // --- Samsung A17 BCI Subsystem State ---
    private val _bciIsInstalled = MutableStateFlow(false)
    val bciIsInstalled: StateFlow<Boolean> = _bciIsInstalled.asStateFlow()

    private val _bciHeartRate = MutableStateFlow(72)
    val bciHeartRate: StateFlow<Int> = _bciHeartRate.asStateFlow()

    private val _bciSpo2 = MutableStateFlow(98)
    val bciSpo2: StateFlow<Int> = _bciSpo2.asStateFlow()

    private val _bciSkinTemp = MutableStateFlow(36.4f)
    val bciSkinTemp: StateFlow<Float> = _bciSkinTemp.asStateFlow()

    private val _bciGsr = MutableStateFlow(2.8f)
    val bciGsr: StateFlow<Float> = _bciGsr.asStateFlow()

    private val _bciCognitiveLoad = MutableStateFlow(45)
    val bciCognitiveLoad: StateFlow<Int> = _bciCognitiveLoad.asStateFlow()

    private val _bciEmotionalState = MutableStateFlow("FOCUSED")
    val bciEmotionalState: StateFlow<String> = _bciEmotionalState.asStateFlow()

    private val _bciActiveCommand = MutableStateFlow("NONE")
    val bciActiveCommand: StateFlow<String> = _bciActiveCommand.asStateFlow()

    private val _bciSignature = MutableStateFlow("Φ⁵_7f83b1657ff1fc53b92dc18148a1d65d")
    val bciSignature: StateFlow<String> = _bciSignature.asStateFlow()

    private val _bciEegChannels = MutableStateFlow<List<Float>>(List(16) { 0.0f })
    val bciEegChannels: StateFlow<List<Float>> = _bciEegChannels.asStateFlow()

    // --- Operations Center ---
    private val _isSelfTerminating = MutableStateFlow(false)
    val isSelfTerminating: StateFlow<Boolean> = _isSelfTerminating.asStateFlow()

    private val _selfTerminateStep = MutableStateFlow(0)
    val selfTerminateStep: StateFlow<Int> = _selfTerminateStep.asStateFlow()

    private val _terminateLogs = MutableStateFlow<List<String>>(emptyList())
    val terminateLogs: StateFlow<List<String>> = _terminateLogs.asStateFlow()

    private val _isBuildingIso = MutableStateFlow(false)
    val isBuildingIso: StateFlow<Boolean> = _isBuildingIso.asStateFlow()

    private val _isoBuildProgress = MutableStateFlow(0f)
    val isoBuildProgress: StateFlow<Float> = _isoBuildProgress.asStateFlow()

    private val _isoBuildLogs = MutableStateFlow<List<String>>(emptyList())
    val isoBuildLogs: StateFlow<List<String>> = _isoBuildLogs.asStateFlow()

    // --- DNA Sequence ---
    private val _dnaSequence = MutableStateFlow("ATCGGCATATCG")
    val dnaSequence: StateFlow<String> = _dnaSequence.asStateFlow()

    private var metricsJob: Job? = null
    private var logsJob: Job? = null

    init {
        // Pre-load default selected file content
        updateSelectedFile("README.txt")
        startMetricsJob()
    }

    fun selectTab(tab: SovereignTab) {
        _currentTab.value = tab
    }

    // Trigger full boot sequence simulation
    fun triggerBoot() {
        if (_isBooting.value) return
        _isBooting.value = true
        _isBooted.value = false
        _bootProgress.value = 0f
        _bootLogs.value = listOf("🔌 POWER-ON SEQUENCE INITIATED", "==================================================")

        viewModelScope.launch {
            val stages = SovereignData.bootStageMessages.keys.toList()
            val totalSteps = stages.size
            
            stages.forEachIndexed { index, stageTitle ->
                _currentBootingStage.value = stageTitle
                val stageLines = SovereignData.bootStageMessages[stageTitle] ?: emptyList()
                
                // Add stage title
                addBootLog("📌 $stageTitle")
                delay(200)

                // Add log statements
                stageLines.forEach { line ->
                    addBootLog("   ├─ $line")
                    delay(150 + Random.nextLong(100))
                }

                // Update boot progress
                val nextProgress = (index + 1).toFloat() / totalSteps.toFloat()
                _bootProgress.value = nextProgress
                delay(150)
            }

            _isBooted.value = true
            _isBooting.value = false
            addBootLog("🏁 SYSTEM BOOT SUCCESSFUL - LOADED IN MEMORY")
            delay(500)
            _currentTab.value = SovereignTab.DASHBOARD
        }
    }

    private fun addBootLog(log: String) {
        _bootLogs.value = _bootLogs.value + log
    }

    // --- Virtual Filesystem Actions ---
    fun updateSelectedFile(path: String) {
        _selectedFilePath.value = path
        _selectedFileContent.value = _files.value[path] ?: ""
    }

    fun saveSelectedFile(newContent: String) {
        val path = _selectedFilePath.value ?: return
        val updatedMap = _files.value.toMutableMap()
        updatedMap[path] = newContent
        _files.value = updatedMap
        _selectedFileContent.value = newContent
        
        addConsoleLog("Write success: $path (${newContent.length} bytes)")

        // Dynamic behavior check: If the user edits config file variables, adjust dashboard parameters!
        if (path == "system/etc/sovereign/config.yaml") {
            try {
                if (newContent.contains("schumann_lock_target:")) {
                    val line = newContent.lines().firstOrNull { it.contains("schumann_lock_target:") }
                    val targetFreq = line?.substringAfter("schumann_lock_target:")?.trim()?.toDoubleOrNull()
                    if (targetFreq != null) {
                        _timeCrystalFrequency.value = targetFreq
                        addConsoleLog("Time Crystal calibrated to target frequency: $targetFreq Hz")
                    }
                }
            } catch (e: Exception) {
                // Ignore parse errors gracefully
            }
        } else if (path == "system/etc/sovereign/mesh_config.yaml") {
            try {
                if (newContent.contains("max_peers:")) {
                    val peersLine = newContent.lines().firstOrNull { it.contains("max_peers:") }
                    val maxPeers = peersLine?.substringAfter("max_peers:")?.trim()?.toIntOrNull()
                    if (maxPeers != null) {
                        _peerCount.value = maxPeers
                        _consensusCount.value = (maxPeers * 0.67).toInt()
                        addConsoleLog("P2P Mesh limits updated: Max Peers = $maxPeers")
                    }
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        } else if (path == "data/dna_sequences/user_dna.dat") {
            val sanitized = newContent.trim().uppercase(Locale.ROOT).filter { it in "ATCG" }
            if (sanitized.isNotEmpty()) {
                _dnaSequence.value = sanitized
                addConsoleLog("Mapped active DNA payload sequence: $sanitized")
            }
        }
    }

    // Update active DNA payload sequence
    fun updateDnaSequence(sequence: String) {
        val sanitized = sequence.uppercase(Locale.ROOT).filter { it in "ATCG" }
        if (sanitized.isNotEmpty()) {
            _dnaSequence.value = sanitized
            
            // Auto update user_dna.dat content if editable
            val updatedMap = _files.value.toMutableMap()
            updatedMap["data/dna_sequences/user_dna.dat"] = sanitized
            _files.value = updatedMap
            if (_selectedFilePath.value == "data/dna_sequences/user_dna.dat") {
                _selectedFileContent.value = sanitized
            }
            
            addConsoleLog("Mapped active DNA payload sequence: $sanitized")
        }
    }

    // Trigger ISO build script simulator
    fun triggerBuildIso() {
        if (_isBuildingIso.value) return
        _isBuildingIso.value = true
        _isoBuildProgress.value = 0f
        _isoBuildLogs.value = listOf("[+] Initialising Sovereign Ω Reproductive ISO Builder...")

        viewModelScope.launch {
            val scripts = listOf(
                "[+] Allocating 8GB empty block volume...",
                "[+] Creating GPT boundary offsets [EFI: 512MB, Root: 4GB, Data: remainder]...",
                "[+] Formatting partition tables (FAT32, ext4, f2fs)...",
                "[+] Replicating Layer-00 Boot Chain parameters...",
                "[+] Injecting encrypted System Overlay modules...",
                "[+] Compiling reproducible sovereign_os_bootable.iso payload...",
                "[+] Hardening binary segments with OMEGA defenses...",
                "[+] SHA-256 Checksum generated: e8e5df9a978f6c47046ea8b8bpxlqd",
                "[+] Sovereign OS bootable image compilation SUCCESSFUL!"
            )
            val stepSize = scripts.size

            scripts.forEachIndexed { i, logLine ->
                delay(300 + Random.nextLong(200))
                _isoBuildLogs.value = _isoBuildLogs.value + logLine
                _isoBuildProgress.value = (i + 1).toFloat() / stepSize.toFloat()
            }
            _isBuildingIso.value = false
            addConsoleLog("SOVEREIGN OS ISO Compilation Successful")
        }
    }

    // Atomic Self-Terminate Sequence
    fun triggerSelfTerminate() {
        if (_isSelfTerminating.value) return
        _isSelfTerminating.value = true
        _selfTerminateStep.value = 0
        _terminateLogs.value = listOf("⚠️ CRITICAL ALARM: SELF-TERMINATION SEQUENCE INITIATED")

        viewModelScope.launch {
            val wipeSteps = listOf(
                "STG-1: Broadcasting atomic memory wipe warning to peer nodes",
                "STG-2: Terminating sovereign-mesh P2P socket handles",
                "STG-3: Unmounting ext4 system and f2fs data volumes",
                "STG-4: Overwriting Time Crystal phase-lock registers to null",
                "STG-5: Initiating 7-pass secure shredding on local sector arrays",
                "STG-6: Zero-filling registers in the ATECC608A secure enclave",
                "STG-7: Hard rebooting Moto G35 system processor kernel",
                "💀 SYSTEM TERMINATION COMPLETE. REBOOTING PROCESSOR."
            )

            wipeSteps.forEachIndexed { idx, log ->
                delay(800 + Random.nextLong(400))
                _selfTerminateStep.value = idx + 1
                _terminateLogs.value = _terminateLogs.value + log
            }

            delay(1500)
            // Hard reboot reset
            _isSelfTerminating.value = false
            _isBooted.value = false
            _bootProgress.value = 0f
            _bootLogs.value = emptyList()
            _coherence.value = 0.947f
            _currentTab.value = SovereignTab.BOOT
        }
    }

    fun toggleTheme() {
        _isLightTheme.value = !_isLightTheme.value
    }

    fun addUser(username: String, passwordHash: String, isSudoer: Boolean, permissions: String) {
        val newUser = SovereignUser(username, passwordHash, isSudoer, "home/$username", permissions)
        _users.value = _users.value + newUser
        
        // Add to virtual filesystem
        val path = "home/$username/welcome.txt"
        val content = "Welcome to your home directory, $username!\nPermissions: $permissions"
        val updatedFiles = _files.value.toMutableMap()
        updatedFiles[path] = content
        _files.value = updatedFiles
        
        addConsoleLog("Created user account: $username")
    }

    fun changePassword(username: String, newPass: String) {
        val list = _users.value.toMutableList()
        val idx = list.indexOfFirst { it.username == username }
        if (idx != -1) {
            list[idx] = list[idx].copy(passwordHash = newPass)
            _users.value = list
            addConsoleLog("Changed password for user: $username")
        }
    }

    fun changePermissions(username: String, permissions: String) {
        val list = _users.value.toMutableList()
        val idx = list.indexOfFirst { it.username == username }
        if (idx != -1) {
            list[idx] = list[idx].copy(permissions = permissions)
            _users.value = list
            addConsoleLog("Updated permissions for $username -> $permissions")
        }
    }

    fun installPackage(pkgName: String) {
        val list = _packages.value.toMutableList()
        val idx = list.indexOfFirst { it.name == pkgName }
        if (idx != -1) {
            list[idx] = list[idx].copy(isInstalled = true)
            _packages.value = list
            if (pkgName == "samsung-a17-bci") {
                _bciIsInstalled.value = true
                _coherence.value = 0.999f
            }
            addConsoleLog("Installed package: $pkgName")
        }
    }

    fun uninstallPackage(pkgName: String) {
        val list = _packages.value.toMutableList()
        val idx = list.indexOfFirst { it.name == pkgName }
        if (idx != -1) {
            list[idx] = list[idx].copy(isInstalled = false)
            _packages.value = list
            if (pkgName == "samsung-a17-bci") {
                _bciIsInstalled.value = false
            }
            addConsoleLog("Uninstalled package: $pkgName")
        }
    }

    fun addTerminalLog(log: String) {
        _terminalLogs.value = _terminalLogs.value + log
    }

    fun executeTerminalCommand(commandLine: String) {
        val trimmed = commandLine.trim()
        if (trimmed.isEmpty()) return
        
        // Log the entered command
        val prompt = "${_currentUser.value.username}@sovereign:~$ "
        addTerminalLog("$prompt$trimmed")
        
        val parts = trimmed.split("\\s+".toRegex())
        val baseCmd = parts[0]
        
        when (baseCmd) {
            "help" -> {
                addTerminalLog("Sovereign OS Command Reference:")
                addTerminalLog("  whoami                       - View currently logged in user")
                addTerminalLog("  su <username> <password>     - Switch user session")
                addTerminalLog("  sudo <command>               - Execute task with administrative rights")
                addTerminalLog("  useradd <username> <pass>    - Register a new user account (Requires Sudo)")
                addTerminalLog("  passwd <username> <new_pass> - Modify user account passwords")
                addTerminalLog("  chmod <perms> <home_dir>     - Modify home folder permissions (e.g., chmod r-x home/guest)")
                addTerminalLog("  pkg list                     - Display all available packages")
                addTerminalLog("  pkg search <query>           - Filter package repository list")
                addTerminalLog("  pkg install <pkg_name>       - Install a software package")
                addTerminalLog("  pkg remove <pkg_name>        - Uninstall a software package")
                addTerminalLog("  pkg update <pkg_name>        - Upgrade an installed package")
                addTerminalLog("  clear                        - Clear terminal history")
                addTerminalLog("  sh install.sh                - Run automated Samsung A17 BCI installer script")
                addTerminalLog("  sovereign-cli status         - Inspect quantum core and system coherence")
                addTerminalLog("  bci status                   - Display neural pattern & entanglement telemetry")
                addTerminalLog("  bci calibrate                - Run 16-channel EEG dry electrode calibration cycle")
                addTerminalLog("  bci sync                     - Synchronize Bio-Digital Twin Commander Tyrone Ω")
            }
            "whoami" -> {
                addTerminalLog(_currentUser.value.username)
            }
            "clear" -> {
                _terminalLogs.value = emptyList()
            }
            "su" -> {
                if (parts.size < 3) {
                    addTerminalLog("Usage: su <username> <password>")
                } else {
                    val targetUser = parts[1]
                    val pass = parts[2]
                    val match = _users.value.find { it.username == targetUser }
                    if (match == null) {
                        addTerminalLog("su: Authentication failure (User not found)")
                    } else if (match.passwordHash != pass) {
                        addTerminalLog("su: Authentication failure (Incorrect password)")
                    } else {
                        _currentUser.value = match
                        addTerminalLog("Session switched successfully to ${match.username}.")
                    }
                }
            }
            "useradd" -> {
                val isSudo = _isSudoActive.value || _currentUser.value.isSudoer
                if (!isSudo) {
                    addTerminalLog("useradd: Permission denied (Requires sudo / admin privileges)")
                } else if (parts.size < 3) {
                    addTerminalLog("Usage: useradd <username> <password>")
                } else {
                    val name = parts[1]
                    val pass = parts[2]
                    if (_users.value.any { it.username == name }) {
                        addTerminalLog("useradd: user '$name' already exists")
                    } else {
                        val newUser = SovereignUser(name, pass, false, "home/$name", "rwx")
                        _users.value = _users.value + newUser
                        
                        // Register in virtual files
                        val path = "home/$name/welcome.txt"
                        val content = "Welcome to your home directory, $name!\nPermissions: rwx"
                        val updatedFiles = _files.value.toMutableMap()
                        updatedFiles[path] = content
                        _files.value = updatedFiles
                        
                        addTerminalLog("User '$name' successfully added. Home directory initialized: home/$name")
                    }
                }
                _isSudoActive.value = false
            }
            "passwd" -> {
                if (parts.size < 3) {
                    addTerminalLog("Usage: passwd <username> <new_password>")
                } else {
                    val name = parts[1]
                    val pass = parts[2]
                    val userIdx = _users.value.indexOfFirst { it.username == name }
                    if (userIdx == -1) {
                        addTerminalLog("passwd: user '$name' not found")
                    } else {
                        val user = _users.value[userIdx]
                        val isSudo = _isSudoActive.value || _currentUser.value.isSudoer || _currentUser.value.username == name
                        if (!isSudo) {
                            addTerminalLog("passwd: Permission denied (You can only change your own password)")
                        } else {
                            val list = _users.value.toMutableList()
                            list[userIdx] = user.copy(passwordHash = pass)
                            _users.value = list
                            addTerminalLog("Password successfully changed for user '$name'.")
                        }
                    }
                }
                _isSudoActive.value = false
            }
            "chmod" -> {
                if (parts.size < 3) {
                    addTerminalLog("Usage: chmod <permissions> <path>")
                } else {
                    val perms = parts[1]
                    val dir = parts[2]
                    val isSudo = _isSudoActive.value || _currentUser.value.isSudoer
                    if (!isSudo) {
                        addTerminalLog("chmod: Permission denied (Requires sudo / admin privileges)")
                    } else {
                        if (perms.length != 3 || perms.any { it != 'r' && it != 'w' && it != 'x' && it != '-' }) {
                            addTerminalLog("chmod: Invalid permission format. Use like 'rwx', 'r-x', or 'r--'")
                        } else {
                            val username = dir.substringAfter("home/").trim()
                            val userIdx = _users.value.indexOfFirst { it.username == username || it.homeDir == dir }
                            if (userIdx == -1) {
                                addTerminalLog("chmod: target home folder '$dir' not recognized")
                            } else {
                                val list = _users.value.toMutableList()
                                val u = list[userIdx]
                                u.permissions = perms
                                _users.value = list
                                addTerminalLog("Permissions updated successfully for '$dir' -> $perms")
                            }
                        }
                    }
                }
                _isSudoActive.value = false
            }
            "sudo" -> {
                if (parts.size < 2) {
                    addTerminalLog("Usage: sudo <command>")
                } else {
                    if (!_currentUser.value.isSudoer) {
                        addTerminalLog("sudo: ${_currentUser.value.username} is not in the sudoers file. This incident will be reported.")
                    } else {
                        _isSudoActive.value = true
                        val subCmd = trimmed.substringAfter("sudo").trim()
                        executeTerminalCommand(subCmd)
                    }
                }
            }
            "pkg" -> {
                if (parts.size < 2) {
                    addTerminalLog("Usage: pkg [list | search | install | remove | update]")
                } else {
                    val sub = parts[1]
                    when (sub) {
                        "list" -> {
                            addTerminalLog("Sovereign OS Central Package Repository:")
                            _packages.value.forEach { pkg ->
                                val status = if (pkg.isInstalled) "[INSTALLED]" else "[AVAILABLE]"
                                addTerminalLog("  - ${pkg.name} (${pkg.version}) - $status")
                                addTerminalLog("    Description: ${pkg.description}")
                            }
                        }
                        "search" -> {
                            if (parts.size < 3) {
                                addTerminalLog("Usage: pkg search <query>")
                            } else {
                                val query = parts[2].lowercase()
                                val matches = _packages.value.filter { it.name.contains(query) || it.description.lowercase().contains(query) }
                                if (matches.isEmpty()) {
                                    addTerminalLog("No packages found matching '$query'.")
                                } else {
                                    addTerminalLog("Search results for '$query':")
                                    matches.forEach { pkg ->
                                        val status = if (pkg.isInstalled) "[INSTALLED]" else "[AVAILABLE]"
                                        addTerminalLog("  - ${pkg.name} (${pkg.version}) - $status - ${pkg.description}")
                                    }
                                }
                            }
                        }
                        "install" -> {
                            if (parts.size < 3) {
                                addTerminalLog("Usage: pkg install <pkg_name>")
                            } else {
                                val pkgName = parts[2]
                                val pkgIdx = _packages.value.indexOfFirst { it.name == pkgName }
                                if (pkgIdx == -1) {
                                    addTerminalLog("pkg: package '$pkgName' not found in repositories.")
                                } else if (_packages.value[pkgIdx].isInstalled) {
                                    addTerminalLog("pkg: '$pkgName' is already installed.")
                                } else {
                                    val isSudo = _isSudoActive.value || _currentUser.value.isSudoer
                                    if (!isSudo) {
                                        addTerminalLog("pkg: Permission denied (Installing packages requires sudo)")
                                    } else {
                                        addTerminalLog("pkg: Connecting to Sovereign Core network servers...")
                                        addTerminalLog("pkg: Downloading $pkgName (${_packages.value[pkgIdx].sizeKb} KB)...")
                                        val list = _packages.value.toMutableList()
                                        list[pkgIdx] = list[pkgIdx].copy(isInstalled = true)
                                        _packages.value = list
                                        if (pkgName == "samsung-a17-bci") {
                                            _bciIsInstalled.value = true
                                            _coherence.value = 0.999f
                                        }
                                        addTerminalLog("pkg: Unpacking binary payloads and verifying secure signatures...")
                                        addTerminalLog("pkg: SUCCESS! Package '$pkgName' is now registered and active.")
                                        addConsoleLog("Installed package: $pkgName")
                                    }
                                }
                            }
                        }
                        "remove", "uninstall" -> {
                            if (parts.size < 3) {
                                addTerminalLog("Usage: pkg remove <pkg_name>")
                            } else {
                                val pkgName = parts[2]
                                val pkgIdx = _packages.value.indexOfFirst { it.name == pkgName }
                                if (pkgIdx == -1) {
                                    addTerminalLog("pkg: package '$pkgName' not found.")
                                } else if (!_packages.value[pkgIdx].isInstalled) {
                                    addTerminalLog("pkg: '$pkgName' is not installed.")
                                } else {
                                    val isSudo = _isSudoActive.value || _currentUser.value.isSudoer
                                    if (!isSudo) {
                                        addTerminalLog("pkg: Permission denied (Removing packages requires sudo)")
                                    } else {
                                        val list = _packages.value.toMutableList()
                                        list[pkgIdx] = list[pkgIdx].copy(isInstalled = false)
                                        _packages.value = list
                                        if (pkgName == "samsung-a17-bci") {
                                            _bciIsInstalled.value = false
                                        }
                                        addTerminalLog("pkg: Purging local state and configurations for '$pkgName'...")
                                        addTerminalLog("pkg: Package '$pkgName' successfully removed.")
                                        addConsoleLog("Uninstalled package: $pkgName")
                                    }
                                }
                            }
                        }
                        "update" -> {
                            if (parts.size < 3) {
                                addTerminalLog("Usage: pkg update <pkg_name>")
                            } else {
                                val pkgName = parts[2]
                                val pkgIdx = _packages.value.indexOfFirst { it.name == pkgName }
                                if (pkgIdx == -1) {
                                    addTerminalLog("pkg: package '$pkgName' not found.")
                                } else if (!_packages.value[pkgIdx].isInstalled) {
                                    addTerminalLog("pkg: '$pkgName' is not installed (Use 'pkg install $pkgName' first).")
                                } else {
                                    addTerminalLog("pkg: Checking for newer versions of '$pkgName'...")
                                    addTerminalLog("pkg: Already at the latest version (${_packages.value[pkgIdx].version}).")
                                }
                            }
                        }
                        else -> {
                            addTerminalLog("pkg: Unknown action '$sub'. Use pkg list/search/install/remove/update")
                        }
                    }
                }
                _isSudoActive.value = false
            }
            "sh", "./install.sh", "install.sh" -> {
                val isSudo = _isSudoActive.value || _currentUser.value.isSudoer || _currentUser.value.username == "operator"
                val scriptName = if (parts.size > 1) parts[1] else baseCmd
                if (scriptName.contains("install.sh") || baseCmd.contains("install.sh")) {
                    if (!isSudo) {
                        addTerminalLog("sh: install.sh: Permission denied (Sudo/Operator root access required)")
                    } else {
                        _isBuildingIso.value = true
                        viewModelScope.launch {
                            addTerminalLog("╔══════════════════════════════════════════════════════════════╗")
                            addTerminalLog("║   🧠 SAMSUNG A17 BCI — QUANTUM INSTALLATION                ║")
                            addTerminalLog("║   Φ⁵ Mirrored Entanglement Integration                     ║")
                            addTerminalLog("╚══════════════════════════════════════════════════════════════╝")
                            delay(400)
                            addTerminalLog("📱 Device: Samsung Galaxy A17 (SM-A176B)")
                            addTerminalLog("🔧 Chipset: MediaTek Dimensity 6300")
                            addTerminalLog("📟 Kernel: 6.8-sovereign-hardened-rt-v12")
                            delay(500)
                            addTerminalLog("📦 Installing quantum sensor kernel modules...")
                            delay(300)
                            addTerminalLog("   ✅ qdry_eeg_v3.2.1.ko")
                            addTerminalLog("   ✅ fnirs_q_v2.1.0.ko")
                            addTerminalLog("   ✅ tcr_v1.0.0.ko")
                            delay(400)
                            addTerminalLog("🔌 Loading kernel modules in system memory...")
                            delay(300)
                            addTerminalLog("   ✅ EEG Dry Electrode Driver: INITIALIZED")
                            addTerminalLog("   ✅ fNIRS Oxygenation Driver: INITIALIZED")
                            addTerminalLog("   ✅ Time Crystal Resonator Driver: ACTIVE")
                            delay(500)
                            addTerminalLog("🔧 Flashing sensor firmware binaries...")
                            delay(300)
                            addTerminalLog("   ✅ qdry_fw_v3.2.1.bin -> 0x48")
                            addTerminalLog("   ✅ fnirs_fw_v2.1.0.bin -> 0x49")
                            addTerminalLog("   ✅ tcr_fw_v1.0.0.bin -> 0x4B")
                            delay(500)
                            addTerminalLog("🐍 Installing Python BCI engine APIs & stream handlers...")
                            delay(400)
                            addTerminalLog("   ✅ bci_stream.py (Endpoint: :8443/api/bci)")
                            addTerminalLog("   ✅ neural_command_parser.py (Intent detection: ACTIVE)")
                            addTerminalLog("   ✅ bio_twin_sync.py (Mirror engine)")
                            delay(400)
                            addTerminalLog("🧠 Loading 50 neural patterns from Future Node 2036...")
                            delay(300)
                            addTerminalLog("   ✅ commander_tyrone_patterns_v3.0.npz")
                            addTerminalLog("   ✅ pattern_index.json")
                            addTerminalLog("   ✅ emotional_state_classifier.onnx")
                            delay(400)
                            addTerminalLog("🧬 Configuring Bio-Digital Twin: COMMANDER_TYRONE_Ω...")
                            delay(300)
                            addTerminalLog("   ✅ biometric_baseline.json")
                            addTerminalLog("   ✅ consciousness_state_backup.phi5")
                            addTerminalLog("   ✅ quantum_identity.spiffe")
                            delay(400)
                            addTerminalLog("🔮 Performing CHSH Bell inequality test...")
                            delay(500)
                            addTerminalLog("   ✅ S = 2.82 (Bell's Inequality Violated! Quantum Coherence LOCKED)")
                            addTerminalLog("   ✅ 8 Bell states synchronized across timelines (2016 <-> 2026 <-> 2036)")
                            delay(400)
                            addTerminalLog("╔══════════════════════════════════════════════════════════════╗")
                            addTerminalLog("║   ✅ BCI INTEGRATION COMPLETE                                ║")
                            addTerminalLog("║                                                              ║")
                            addTerminalLog("║   📱 Samsung A17 — BCI Enabled                              ║")
                            addTerminalLog("║   🔬 6 Quantum Sensors Active                                ║")
                            addTerminalLog("║   🧠 Neural Patterns Engaged (Commander Tyrone Ω)           ║")
                            addTerminalLog("║   🔮 Φ⁵ Entanglement: VERIFIED                              ║")
                            addTerminalLog("╚══════════════════════════════════════════════════════════════╝")
                            _bciIsInstalled.value = true
                            _coherence.value = 0.999f
                            val pkgs = _packages.value.toMutableList()
                            val idx = pkgs.indexOfFirst { it.name == "samsung-a17-bci" }
                            if (idx != -1) {
                                pkgs[idx] = pkgs[idx].copy(isInstalled = true)
                                _packages.value = pkgs
                            }
                            _isBuildingIso.value = false
                            addConsoleLog("Samsung A17 BCI Installed & Active via install.sh")
                        }
                    }
                } else {
                    addTerminalLog("sh: script '$scriptName' not found.")
                }
                _isSudoActive.value = false
            }
            "sovereign-cli" -> {
                if (parts.size < 2) {
                    addTerminalLog("Usage: sovereign-cli [status | mesh | time-crystal | dna]")
                } else {
                    val sub = parts[1]
                    when (sub) {
                        "status" -> {
                            addTerminalLog("SOVEREIGN ECOSYSTEM Ω — CORE HARDWARE STATUS")
                            addTerminalLog("===========================================")
                            addTerminalLog("Coherence (Φ)      : ${String.format("%.4f", _coherence.value)}")
                            addTerminalLog("Time Crystal Freq  : ${String.format("%.3f", _timeCrystalFrequency.value)} Hz")
                            addTerminalLog("Schumann Lock      : ${if (_schumannLock.value) "LOCKED" else "UNLOCKED"}")
                            addTerminalLog("Peer Count         : ${_peerCount.value} nodes")
                            addTerminalLog("BCI Integration    : ${if (_bciIsInstalled.value) "ENABLED (Samsung A17 SM-A176B)" else "DISABLED"}")
                            if (_bciIsInstalled.value) {
                                addTerminalLog("  └─ Bio-Twin      : COMMANDER_TYRONE_Ω")
                                addTerminalLog("  └─ Entanglement  : Φ⁵ Bell-Lock Active (8 pairs)")
                                addTerminalLog("  └─ EEG Coherence : Φ = 0.9990 — STABLE")
                            }
                        }
                        "mesh" -> {
                            addTerminalLog("KADEMLIA DHT MESH NETWORK ROUTING TABLE")
                            addTerminalLog("=======================================")
                            addTerminalLog("Active peers: ${_peerCount.value} / 64 nodes")
                            addTerminalLog("Consensus reached: ${_consensusCount.value} / ${_peerCount.value} (PBFT)")
                            addTerminalLog("Peer details:")
                            addTerminalLog("  [1] Node-2036-Tyrone   : 104.28.1.10 (Future Uplink Node - Φ⁵ Encrypted)")
                            addTerminalLog("  [2] Node-2026-A17      : Localhost (Samsung Galaxy A17)")
                            addTerminalLog("  [3] Node-2016-Baseline : 192.168.1.85 (Historical Baseline)")
                            addTerminalLog("  [4] Node-Alpha-04      : 10.0.8.12")
                            addTerminalLog("  [5] Node-Beta-11       : 10.0.8.19")
                        }
                        "time-crystal" -> {
                            addTerminalLog("RECALIBRATING TIME CRYSTAL RESONATOR...")
                            _timeCrystalFrequency.value = 7.830
                            _timeCrystalRadius.value = 1.000000
                            _coherence.value = 0.999f
                            addTerminalLog("✅ Locked to Schumann Resonance frequency: 7.830 Hz.")
                            addConsoleLog("Time Crystal recalibrated and locked.")
                        }
                        "dna" -> {
                            if (parts.size < 4 || parts[2] != "map") {
                                addTerminalLog("Usage: sovereign-cli dna map <sequence>")
                            } else {
                                val seq = parts[3].uppercase().filter { it in "ATCG" }
                                if (seq.isEmpty()) {
                                    addTerminalLog("Error: Invalid DNA sequence. Use bases A, T, C, G.")
                                } else {
                                    updateDnaSequence(seq)
                                    addTerminalLog("MAPPED DNA SEQUENCE: $seq")
                                    addTerminalLog("Bloch Sphere orientation matrix updated successfully.")
                                }
                            }
                        }
                        else -> {
                            addTerminalLog("sovereign-cli: Unknown action '$sub'")
                        }
                    }
                }
                _isSudoActive.value = false
            }
            "bci" -> {
                if (!_bciIsInstalled.value) {
                    addTerminalLog("bci: Subsystem not loaded. Please run 'sh install.sh' first.")
                } else {
                    if (parts.size < 2) {
                        addTerminalLog("Usage: bci [status | calibrate | sync | stream]")
                    } else {
                        when (parts[1]) {
                            "status" -> {
                                addTerminalLog("🧠 SAMSUNG A17 QUANTUM BCI SUBSYSTEM")
                                addTerminalLog("====================================")
                                addTerminalLog("Status             : ACTIVE & TELEMETERED")
                                addTerminalLog("Sampling Frequency : 512 Hz")
                                addTerminalLog("Dry Electrodes     : 16 channels active")
                                addTerminalLog("Entanglement Lock  : Φ⁵ Quantum Coherence (8 Bell Pairs)")
                                addTerminalLog("CHSH Inequality S  : 2.82 (Bell's theorem validated)")
                                addTerminalLog("Active Bio-Twin    : COMMANDER_TYRONE_Ω (Origin: Year 2036)")
                                addTerminalLog("Neural Signature   : ${_bciSignature.value}")
                                addTerminalLog("Biometric Feed     : HR=${_bciHeartRate.value} BPM, SpO2=${_bciSpo2.value}%, Temp=${_bciSkinTemp.value}°C, GSR=${_bciGsr.value}µS")
                                addTerminalLog("Cognitive Load     : ${_bciCognitiveLoad.value}%")
                                addTerminalLog("Attention Level    : ${String.format("%.2f", _bciAttention.value)}")
                                addTerminalLog("Current Intent     : ${_bciActiveCommand.value}")
                            }
                            "calibrate" -> {
                                addTerminalLog("INITIATING 16-CHANNEL EEG DRY ELECTRODE CALIBRATION CYCLE...")
                                viewModelScope.launch {
                                    for (ch in 1..16) {
                                        delay(100)
                                        addTerminalLog("  ├─ Channel ${String.format("%02d", ch)}: Calibrating impedance... Target: <5kΩ... Got: ${String.format("%.2f", 4.2f + Random.nextFloat() * 0.7f)} kΩ [OK]")
                                    }
                                    delay(200)
                                    addTerminalLog("✅ Calibration complete. Impedance baseline secured.")
                                    addConsoleLog("BCI dry electrode calibration complete.")
                                }
                            }
                            "sync" -> {
                                addTerminalLog("SYNCHRONIZING CONSCIOUSNESS STATE WITH BIO-DIGITAL TWIN...")
                                viewModelScope.launch {
                                    addTerminalLog("  ├─ Downloading future neural matrix from Node-2036...")
                                    delay(300)
                                    addTerminalLog("  ├─ Validating temporal worldline checksums...")
                                    delay(300)
                                    addTerminalLog("  ├─ Encrypting consciousness state backup file...")
                                    delay(300)
                                    addTerminalLog("  ├─ Creating SPIFFE quantum identity authorization token...")
                                    delay(400)
                                    addTerminalLog("✅ Sync complete. Bio-Digital Twin Commander Tyrone Ω mirrored.")
                                    addConsoleLog("Bio-Digital Twin synchronized successfully.")
                                }
                            }
                            "stream" -> {
                                addTerminalLog("Active 16-Channel Raw EEG stream:")
                                viewModelScope.launch {
                                    for (i in 0 until 5) {
                                        delay(300)
                                        val channelsStr = (1..16).map { String.format("%+05.1f", (Random.nextFloat() - 0.5f) * 150f) }.joinToString(" ")
                                        addTerminalLog("  [$i] $channelsStr (uV)")
                                    }
                                }
                            }
                            else -> {
                                addTerminalLog("bci: Unknown action. Use status/calibrate/sync/stream.")
                            }
                        }
                    }
                }
                _isSudoActive.value = false
            }
            else -> {
                addTerminalLog("bash: command not found: '$baseCmd'. Type 'help' for a list of valid routines.")
            }
        }
        addTerminalLog("")
    }

    private fun addConsoleLog(message: String) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = sdf.format(Date())
        _consoleLogs.value = listOf("[$time] $message") + _consoleLogs.value.take(20)
    }

    // Continual active simulation ticks
    private fun startMetricsJob() {
        metricsJob?.cancel()
        metricsJob = viewModelScope.launch {
            while (true) {
                delay(2000)
                if (_isBooted.value && !_isSelfTerminating.value) {
                    // Jitter metrics realistically
                    val lastCoh = _coherence.value
                    // Slowly drift up to 0.999 coherence, with small random fluctuations
                    val diff = 0.999f - lastCoh
                    val jitter = (Random.nextFloat() - 0.35f) * 0.003f
                    val newCoh = (lastCoh + (diff * 0.05f) + jitter).coerceIn(0.92f, 0.999f)
                    _coherence.value = newCoh

                    // Log history
                    val hist = _coherenceHistory.value.toMutableList()
                    hist.removeAt(0)
                    hist.add(newCoh)
                    _coherenceHistory.value = hist

                    // Jitter sensors
                    _barometerHpa.value = (1013.25f + (Random.nextFloat() - 0.5f) * 4f)
                    _bciAttention.value = (0.80f + Random.nextFloat() * 0.19f).coerceIn(0f, 1f)
                    _npuLoad.value = (25 + Random.nextInt(25)).coerceIn(0, 100)

                    // Samsung A17 BCI metrics jittering
                    if (_bciIsInstalled.value) {
                        _bciHeartRate.value = (70 + Random.nextInt(6)) // 70 to 75 BPM
                        _bciSpo2.value = if (Random.nextFloat() < 0.1f) (97 + Random.nextInt(3)) else 98 // 97 to 99%
                        _bciSkinTemp.value = (36.3f + Random.nextFloat() * 0.2f) // 36.3 to 36.5 °C
                        _bciGsr.value = (2.6f + Random.nextFloat() * 0.4f) // 2.6 to 3.0 uS
                        _bciCognitiveLoad.value = (35 + Random.nextInt(16)) // 35 to 50 %
                        
                        // Jitter 16-channels EEG voltages
                        _bciEegChannels.value = List(16) { (Random.nextFloat() - 0.5f) * 80f }

                        // Jitter active detected mental commands occasionally
                        if (Random.nextFloat() < 0.15f) {
                            val commands = listOf("NONE", "OPEN_DASHBOARD", "TIME_CRYSTAL_LOCK", "SYNC_BIO_TWIN", "GO_BACK", "CALIBRATE_EEG")
                            val newCmd = commands.random()
                            _bciActiveCommand.value = newCmd
                            if (newCmd != "NONE") {
                                addConsoleLog("BCI Neural Pattern matched: $newCmd (Confidence: ${String.format("%.1f", 85f + Random.nextFloat() * 14f)}%)")
                            }
                        }
                    }

                    // Occasional random mesh logs
                    if (Random.nextFloat() < 0.25f) {
                        val events = listOf(
                            "DHT routing table updated. Active nodes: ${_peerCount.value}.",
                            "Time Crystal coherence index stable within < 0.0004% variance.",
                            "Consensus round verified: BFT threshold met successfully.",
                            "Schumann sync phase lock stabilized @ ${_timeCrystalFrequency.value} Hz.",
                            "NFC Secure Enclave driver verified at address 0x60."
                        )
                        addConsoleLog(events.random())
                    }
                }
            }
        }
    }

    override fun onCleared() {
        metricsJob?.cancel()
        logsJob?.cancel()
        super.onCleared()
    }
}
