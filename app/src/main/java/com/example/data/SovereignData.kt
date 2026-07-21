package com.example.data

object SovereignData {

    // Initial files map
    val defaultFiles: Map<String, String> = mapOf(
        "README.txt" to """
Sovereign Ecosystem Ω — Bootable System Image
===================================================
Device Target  : Moto G35 5G / ARM64 / x86_64 PC
Architecture   : UEFI Secure Boot Enabled (db.auth)
Kernel Build   : 6.8-sovereign-hardened-rt-v12
Security Level : LEVEL-5 (Quantum Crypto Enabled)
Mesh Network   : Kademlia DHT Fully Enforced
Coherence Lock : Schumann Resonance Locked @ 7.83 Hz

Status: OPERATIONAL
WARNING: Authorised operators only.
        """.trimIndent(),

        "EFI/sovereign/grub.cfg" to """
# GRUB2 Configuration for Sovereign Ecosystem Ω
set default="0"
set timeout=3

menuentry "Sovereign OS Kernel 6.8 (ARM64 / Moto G35)" {
    search --no-floppy --fs-uuid --set=root efi_partition_id
    linux /linux/vmlinuz-6.8-sovereign root=/dev/sda2 quiet splash coherent_mode=1
    initrd /linux/initramfs-sovereign.img
}

menuentry "Sovereign OS Kernel 6.8 (x86_64 UEFI PC)" {
    linux /linux/vmlinuz-6.8-x86_64 root=/dev/sdb2 quiet splash
    initrd /linux/initramfs-sovereign.img
}

menuentry "Sovereign Recovery Environment (Fallback)" {
    linux /linux/vmlinuz-6.8-sovereign root=/dev/sda2 fallback=1
    initrd /linux/initramfs-sovereign.img
}
        """.trimIndent(),

        "system/etc/sovereign/config.yaml" to """
# Main Sovereign OS Configuration
system:
  version: "1.0.0-omega"
  codename: "Coherence"
  operator: "operator@sovereign"
  security_enclave: "ATECC608A"
  schumann_lock_target: 7.830  # Target frequency in Hz

engine:
  threads: 8
  quantum_mode: true
  emotion_detection: true
  npu_allocation: 0.85
  debug_level: "INFO"
        """.trimIndent(),

        "system/etc/sovereign/mesh_config.yaml" to """
# P2P Mesh Network Configuration
mesh:
  protocol: "Kademlia-DHT"
  routing_table: "/data/mesh_state/routing_table.db"
  max_peers: 64
  min_peers: 16
  consensus_algorithm: "Byzantine-Fault-Tolerant-PBFT"
  consensus_threshold_pct: 0.67
  encryption: "PQ-NTRU-Prime"
  coherence_coefficient_target: 0.999
        """.trimIndent(),

        "system/etc/sovereign/time_crystal.yaml" to """
# Time Crystal Stabilizer settings
time_crystal:
  radius: 1.000000
  phase_lock: "Schumann Resonance"
  harmonic_factor: 1.0
  drift_tolerance: 0.0004  # in percent
  calibration_cycle_seconds: 5
        """.trimIndent(),

        "system/etc/sovereign/dna_mapping.yaml" to """
# DNA Sequence to Bloch Sphere Mapping Matrix
# Bases are mapped into spherical coordinates: theta [0, PI], phi [0, 2*PI]
matrix:
  A: { theta: 0.000000, phi: 0.000000, color: "#00FF88" }  # Adenine
  T: { theta: 3.141592, phi: 0.000000, color: "#FF00FF" }  # Thymine
  C: { theta: 1.570796, phi: 0.000000, color: "#44AAFF" }  # Cytosine
  G: { theta: 1.570796, phi: 1.570796, color: "#FFAA00" }  # Guanine
        """.trimIndent(),

        "system/etc/sovereign/sensors.yaml" to """
# Hardware Sensor Calibration Data
calibration:
  barometer:
    baseline_hpa: 1013.25
    variance_hpa: 5.0
  bci_sensor:
    bandpass_hz: [0.5, 45.0]
    default_attention: 0.87
  nfc_secure_element:
    driver: "atecc608a.ko"
    address: "0x60"
        """.trimIndent(),

        "data/dna_sequences/user_dna.dat" to "ATCGGCATATCG",

        "13_BUILD/build_iso.sh" to """
#!/bin/bash
# BUILD REPRODUCIBLE SOVEREIGN OS BOOTABLE IMAGE
echo "[+] Starting Sovereign OS Build Toolchain v1.0"
echo "[+] Creating blank 8GB disk image: sovereign_os.img"
dd if=/dev/zero of=sovereign_os.img bs=1M count=8192

echo "[+] Creating GPT partitions..."
sgdisk -o sovereign_os.img
sgdisk -n 1:2048:+512M -t 1:EF00 sovereign_os.img    # EFI Boot
sgdisk -n 2:0:+4G -t 2:8300 sovereign_os.img          # ext4 Root System
sgdisk -n 3:0:0 -t 3:8300 sovereign_os.img            # f2fs User Data

echo "[+] Formatting filesystems..."
# Simulating loopback mounting
echo "[+] Copying EFI boot chain components..."
echo "[+] Hardening kernel with OMEGA defense patch..."
echo "[+] Building complete. ISO generated: sovereign_os_bootable.iso"
        """.trimIndent()
    )

    // Virtual Directory Hierarchy structure
    val directoryStructure: List<String> = listOf(
        "README.txt",
        "EFI/BOOT/BOOTAA64.EFI",
        "EFI/BOOT/BOOTX64.EFI",
        "EFI/sovereign/grub.cfg",
        "EFI/sovereign/systemd-boot.conf",
        "EFI/sovereign/kernel.cmdline",
        "EFI/android/boot.img",
        "EFI/android/vendor_boot.img",
        "EFI/android/dtbo.img",
        "EFI/linux/vmlinuz-6.8-sovereign",
        "system/bin/init",
        "system/bin/sovereign_init",
        "system/bin/mesh_daemon",
        "system/bin/time_crystal_daemon",
        "system/etc/sovereign/config.yaml",
        "system/etc/sovereign/mesh_config.yaml",
        "system/etc/sovereign/time_crystal.yaml",
        "system/etc/sovereign/dna_mapping.yaml",
        "system/etc/sovereign/sensors.yaml",
        "system/etc/systemd/system/sovereign-mesh.service",
        "system/etc/systemd/system/time-crystal.service",
        "data/dna_sequences/user_dna.dat",
        "data/mesh_state/routing_table.db",
        "13_BUILD/build_iso.sh"
    )

    // Log messages for the boot stages
    val bootStageMessages: Map<String, List<String>> = mapOf(
        "UEFI Firmware Initialization" to listOf(
            "CPU: ARM Cortex-A78 @ 2.8GHz (8 cores)",
            "RAM: 8GB LPDDR5 @ 6400MHz",
            "NPU: 6 TOPS available",
            "Secure Enclave: ATECC608A detected",
            "Display: 6.7\" 120Hz OLED screen"
        ),
        "Secure Boot Verification" to listOf(
            "Verifying PK/KEK/db signatures...",
            "BOOTAA64.EFI: ✅ SIGNATURE VALID",
            "sovereign/grub.cfg: ✅ HASH MATCH",
            "boot.img: ✅ VERIFIED BOOT PASS"
        ),
        "Bootloader Selection" to listOf(
            "Loading GRUB2 from EFI/sovereign/",
            "Parsing kernel.cmdline...",
            "Selected: Sovereign Ω Kernel 6.8",
            "Command line: root=/dev/sda2 quiet splash"
        ),
        "Kernel Loading" to listOf(
            "Decompressing vmlinuz-6.8-sovereign...",
            "Loading Device Tree Blob: moto_g35.dtb",
            "Initializing drivers: NFC, BCI, GPU, NPU",
            "Loading kernel modules: qnls_engine, emotion_engine",
            "Kernel command line parsed successfully"
        ),
        "Initramfs Setup" to listOf(
            "Mounting initramfs-sovereign.img",
            "Running /init core script",
            "Checking root filesystem: ext4 on /dev/sda2",
            "fsck: ext4 on /dev/sda2 is CLEAN"
        ),
        "System Initialization" to listOf(
            "systemd 255 starting...",
            "Mounting filesystems (ext4 + f2fs + tmpfs)",
            "Setting up cgroups v2 resource slices",
            "Loading system modules from /lib/modules",
            "Starting udev device manager daemon"
        ),
        "Service Startup" to listOf(
            "Starting time-crystal.service... ✅ [OK]",
            "Starting sovereign-mesh.service... ✅ [OK]",
            "Starting sensor-bridge.service... ✅ [OK]",
            "Starting bloch-visualizer.service... ✅ [OK]",
            "Starting dashboard-api.service... ✅ [OK]"
        ),
        "Mesh Network Initialization" to listOf(
            "P2P Kademlia DHT: 64 active nodes discovered",
            "BFT Consensus: 43/64 threshold met",
            "Time Crystal: r=1.0 locked @ 7.83Hz",
            "Schumann sync: ✅ Phase locked",
            "Coherence: Φ = 0.947 → 0.999"
        ),
        "Dashboard Activation" to listOf(
            "Starting WebSocket server on port :8443",
            "Serving dashboard from /usr/share/sovereign/",
            "Bloch sphere renderer: ACTIVE (Custom Canvas)",
            "Real-time metrics stream: ENABLED",
            "API endpoints active: /api/mesh, /api/crystal"
        ),
        "System Ready" to listOf(
            "All systems nominal on Moto G35",
            "Dashboard operational locally at https://localhost:8443",
            "Coherence: Φ = 0.999 — STABLE",
            "Sovereign Ecosystem Ω: ONLINE"
        )
    )
}
