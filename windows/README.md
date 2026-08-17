# V-Link Windows Desktop Client & Installer

This directory contains the Inno Setup script, build scripts, and resources to package V-Link into a standalone Windows installer (`.exe`).

---

## 🚀 How to Build the Installer

### Option 1: Automatic Cloud Build (GitHub Actions)
1. Push your repository to GitHub (e.g. `V-Link-Desktop`).
2. Go to the **Actions** tab on your GitHub repository.
3. Select **"Build Windows Desktop Installer (.exe)"** and click **Run workflow**.
4. When finished, download the **`V-Link-Setup-1.0.0.exe`** from the **Artifacts** section at the bottom of the run page.

---

### Option 2: Local Build on Windows PC
1. Download and install [Inno Setup 6 (Free)](https://jrsoftware.org/isdl.php).
2. Right-click `build_installer.bat` and select **Run as administrator** (or double-click it).
3. The generated installer will be saved to:
   ```
   windows\Output\V-Link-Setup-1.0.0.exe
   ```

---

## 📦 What the Installer Does:
- Installs all application components to `C:\Program Files\V-Link`.
- Creates **Desktop Home Screen shortcut** (`V-Link`).
- Adds entry into Windows **Start Menu**.
- Includes complete Windows Uninstaller support (`unins000.exe`).
