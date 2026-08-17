# V-Link Windows Desktop Installer Configuration

This directory contains the Inno Setup script and automation tools to generate a **native Windows `.exe` setup installer** (`V-Link-Setup-1.0.0.exe`).

---

## 🛠️ Features of the Windows Installer
1. **Installs to Program Files**: Automatically installs to `C:\Program Files\V-Link` (or local AppData if run without Admin).
2. **Desktop Shortcut**: Adds a checkbox during setup to place an icon directly on the user's Desktop.
3. **Start Menu Integration**: Creates a Start Menu folder and shortcut.
4. **Built-in Uninstaller**: Registers a standard Windows uninstaller (`unins000.exe`) in Windows "Apps & Features" / "Add or Remove Programs".
5. **Launch on Finish**: Provides an option to immediately launch the app after setup completes.

---

## 🚀 How to Build the `.exe` Installer on Windows

### Option 1: Double-Click Batch Script
1. Download and install **[Inno Setup 6](https://jrsoftware.org/isdl.php)** (Free & Open Source).
2. Double-click `build_installer.bat` in this folder.
3. Your installer will be generated in `windows\Output\V-Link-Setup-1.0.0.exe`.

### Option 2: Automated via GitHub Actions
1. Push the project to GitHub (via the **GitHub** button in Google AI Studio).
2. Go to the **Actions** tab on your GitHub repository.
3. The `Build Windows Desktop Installer (.exe)` workflow will run automatically and provide the downloadable installer `.exe` under **Artifacts**.
