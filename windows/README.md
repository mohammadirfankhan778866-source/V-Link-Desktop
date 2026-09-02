# V-Link Windows Desktop Client & Multi-Installer Suite

This directory contains the complete Windows installer packaging configuration for V-Link, supporting Inno Setup, NSIS, WiX Toolset (.msi), and automated PowerShell deployment.

---

## 📦 Available Windows Installer Options:

| Installer Type | Source Script | Description |
| :--- | :--- | :--- |
| **Inno Setup 6 (.exe)** | `windows/vlink_setup.iss` | Modern graphical wizard, desktop & Start Menu shortcuts, solid LZMA2 compression. |
| **NSIS 3 (.exe)** | `windows/vlink_installer.nsi` | Lightweight Nullsoft installer with administrative privilege elevation. |
| **WiX Toolset v4 (.msi)** | `windows/VLinkSetup.wxs` | Enterprise-grade Windows Installer package (`.msi`) for Group Policy / Intune. |
| **PowerShell Script (.ps1)** | `windows/install_vlink.ps1` | Silent unattended deployment, update check, and registry registration. |

---

## 🚀 How to Build via Cloud (CI/CD)

### 1. AppVeyor CI (`appveyor.yml`)
- Connect your GitHub repository to [AppVeyor](https://www.appveyor.com/).
- AppVeyor runs on Windows Server 2022 image, installs Inno Setup & NSIS, and outputs `.exe` installer artifacts in your **Artifacts** tab.

### 2. GitHub Actions (`.github/workflows/build-windows-installer.yml`)
- Push your repo to GitHub ➔ Go to **Actions** tab ➔ Download the generated **`V-Link-Setup-1.0.0.exe`**.

---

## 🛠️ Local PowerShell Automation Commands:

Run these in Windows PowerShell:

```powershell
# 1. Standard Interactive Install
.\windows\install_vlink.ps1

# 2. Silent Unattended Install (for IT scripts)
.\windows\install_vlink.ps1 -Silent

# 3. Check for Remote Updates against version.json
.\windows\install_vlink.ps1 -CheckUpdate

# 4. Clean Uninstall
.\windows\install_vlink.ps1 -Uninstall
```

---

## 🔄 Version Check System:
- **Local version tracking file**: `windows/version.json`
- **Remote check endpoint**: `UpdateService.kt` compares semantic versions (`1.0.0` vs remote `1.2.5`) to prompt the user with release notes and 1-click update download buttons in Settings.
