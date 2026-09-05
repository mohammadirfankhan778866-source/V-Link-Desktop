<#
.SYNOPSIS
    V-Link Desktop Windows Installer & Management Script
.DESCRIPTION
    Robust PowerShell deployment script that handles:
    - Silent / Unattended Installation
    - Custom target install directory support
    - Desktop Shortcut creation
    - Start Menu Registration
    - Uninstallation & Registry Cleanup
    - Version comparison with remote update manifest
.PARAMETER Silent
    Runs the installation silently without user prompts.
.PARAMETER InstallDir
    Target directory (Defaults to "$env:ProgramFiles\V-Link" or "$env:LOCALAPPDATA\Programs\V-Link")
.PARAMETER Uninstall
    Uninstalls V-Link and removes shortcuts and registry entries.
.PARAMETER CheckUpdate
    Compares local version against remote version.json
.EXAMPLE
    .\install_vlink.ps1 -Silent
    .\install_vlink.ps1 -InstallDir "D:\Apps\V-Link"
    .\install_vlink.ps1 -Uninstall
    .\install_vlink.ps1 -CheckUpdate
#>

[CmdletBinding()]
param (
    [switch]$Silent = $false,
    [string]$InstallDir = "",
    [switch]$Uninstall = $false,
    [switch]$CheckUpdate = $false
)

$ErrorActionPreference = "Stop"

# Application Metadata
$AppName = "V-Link"
$AppVersion = "1.0.0"
$Publisher = "V-Link Messenger Inc."
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Determine default target directory based on privileges
if ([string]::IsNullOrWhiteSpace($InstallDir)) {
    $isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
    if ($isAdmin) {
        $InstallDir = Join-Path $env:ProgramFiles "V-Link"
    } else {
        $InstallDir = Join-Path $env:LOCALAPPDATA "Programs\V-Link"
    }
}

function Write-Log {
    param([string]$Message, [string]$Type = "INFO")
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $color = switch ($Type) {
        "SUCCESS" { "Green" }
        "WARN"    { "Yellow" }
        "ERROR"   { "Red" }
        default   { "Cyan" }
    }
    if (-not $Silent -or $Type -eq "ERROR") {
        Write-Host "[$timestamp] [$Type] $Message" -ForegroundColor $color
    }
}

# =========================================================================
#  1. Version Check Routine
# =========================================================================
if ($CheckUpdate) {
    Write-Log "Checking for V-Link updates..." "INFO"
    $versionUrl = "https://raw.githubusercontent.com/mohammadirfankhan778866-source/V-Link-Desktop/main/windows/version.json"
    try {
        $remoteManifest = Invoke-RestMethod -Uri $versionUrl -Method Get -TimeoutSec 5
        Write-Log "Current Installed Version: $AppVersion" "INFO"
        Write-Log "Latest Remote Version:     $($remoteManifest.version)" "INFO"
        
        $currParts = $AppVersion.Split('.') | ForEach-Object { [int]$_ }
        $remParts  = $remoteManifest.version.Split('.') | ForEach-Object { [int]$_ }
        
        $hasUpdate = $false
        for ($i = 0; $i -lt [Math]::Max($currParts.Count, $remParts.Count); $i++) {
            $c = if ($i -lt $currParts.Count) { $currParts[$i] } else { 0 }
            $r = if ($i -lt $remParts.Count) { $remParts[$i] } else { 0 }
            if ($r -gt $c) { $hasUpdate = $true; break }
            if ($r -lt $c) { break }
        }
        
        if ($hasUpdate) {
            Write-Log "An update is available: v$($remoteManifest.version)!" "SUCCESS"
            Write-Log "Download URL: $($remoteManifest.downloadUrl)" "SUCCESS"
            Write-Log "Release notes: $($remoteManifest.releaseNotes -join ', ')" "INFO"
        } else {
            Write-Log "V-Link is up to date (v$AppVersion)." "SUCCESS"
        }
    } catch {
        Write-Log "Could not reach remote version server ($($_.Exception.Message)). Checked local version: v$AppVersion" "WARN"
    }
    exit 0
}

# =========================================================================
#  2. Uninstallation Routine
# =========================================================================
if ($Uninstall) {
    Write-Log "Starting uninstallation of $AppName..." "INFO"
    
    # Remove Desktop Shortcut
    $desktopShortcut = Join-Path ([Environment]::GetFolderPath("Desktop")) "$AppName.lnk"
    if (Test-Path $desktopShortcut) {
        Remove-Item -Path $desktopShortcut -Force
        Write-Log "Removed Desktop shortcut." "SUCCESS"
    }
    
    # Remove Start Menu Shortcut
    $startMenuDir = Join-Path ([Environment]::GetFolderPath("Programs")) "$AppName"
    if (Test-Path $startMenuDir) {
        Remove-Item -Path $startMenuDir -Recurse -Force
        Write-Log "Removed Start Menu folder." "SUCCESS"
    }
    
    # Remove Installation Files
    if (Test-Path $InstallDir) {
        Remove-Item -Path $InstallDir -Recurse -Force
        Write-Log "Removed files at $InstallDir" "SUCCESS"
    }
    
    # Clean Registry
    try {
        Remove-Item -Path "HKCU:\Software\$AppName" -Recurse -ErrorAction SilentlyContinue
    } catch {}
    
    Write-Log "$AppName has been cleanly uninstalled from this PC." "SUCCESS"
    exit 0
}

# =========================================================================
#  3. Installation Routine
# =========================================================================
Write-Log "==========================================================" "INFO"
Write-Log "          $AppName Desktop Installer v$AppVersion         " "SUCCESS"
Write-Log "==========================================================" "INFO"
Write-Log "Target Directory: $InstallDir" "INFO"

# Step 1: Create Destination Directories
if (-not (Test-Path $InstallDir)) {
    New-Item -ItemType Directory -Path $InstallDir -Force | Out-Null
}

$targetExe = Join-Path $InstallDir "V-Link.exe"

# Step 2: Copy Files
$sourceDist = Join-Path (Split-Path -Parent $ScriptDir) "dist\V-Link"
if (Test-Path $sourceDist) {
    Copy-Item -Path "$sourceDist\*" -Destination $InstallDir -Recurse -Force
    Write-Log "Application payload staged to $InstallDir." "SUCCESS"
}

# Ensure V-Link.exe binary exists in target directory
if (-not (Test-Path $targetExe)) {
    $csSource = Join-Path $ScriptDir "VLinkLauncher.cs"
    if (Test-Path $csSource) {
        Write-Log "Compiling V-Link.exe native binary..." "INFO"
        try {
            Add-Type -TypeDefinition (Get-Content $csSource -Raw) -OutputAssembly $targetExe -OutputType WindowsGUI -ReferencedAssemblies "System.Windows.Forms","System.Drawing"
            Write-Log "Compiled V-Link.exe directly into $InstallDir." "SUCCESS"
        } catch {
            Write-Log "Add-Type compilation: $($_.Exception.Message)" "WARN"
        }
    }
    
    # Fallback to creating a launcher script if compilation failed
    if (-not (Test-Path $targetExe)) {
        $appCmd = Join-Path $InstallDir "V-Link.cmd"
        @"
@echo off
start "" "https://v-link.chat"
"@ | Out-File -FilePath $appCmd -Encoding ascii
        $targetExe = $appCmd
    }
}

# Copy version.json and app.ico for local comparison and shortcut icon
$versionSrc = Join-Path $ScriptDir "version.json"
if (Test-Path $versionSrc) {
    Copy-Item -Path $versionSrc -Destination (Join-Path $InstallDir "version.json") -Force
}
$icoSrc = Join-Path $ScriptDir "app.ico"
$targetIco = Join-Path $InstallDir "app.ico"
if (Test-Path $icoSrc) {
    Copy-Item -Path $icoSrc -Destination $targetIco -Force
}

# Step 3: Create Desktop Shortcut with custom V-Link logo
$wshShell = New-Object -ComObject WScript.Shell
$desktopPath = [Environment]::GetFolderPath("Desktop")
$desktopShortcutPath = Join-Path $desktopPath "$AppName.lnk"

$shortcut = $wshShell.CreateShortcut($desktopShortcutPath)
$shortcut.TargetPath = $targetExe
$shortcut.WorkingDirectory = $InstallDir
$shortcut.Description = "V-Link Secure Desktop Messenger"
if (Test-Path $targetIco) {
    $shortcut.IconLocation = "$targetIco,0"
}
$shortcut.Save()
Write-Log "Desktop shortcut created with V-Link icon at: $desktopShortcutPath" "SUCCESS"

# Step 4: Create Start Menu Programs Shortcut with custom V-Link logo
$startMenuPrograms = [Environment]::GetFolderPath("Programs")
$vlinkStartMenuDir = Join-Path $startMenuPrograms "$AppName"
if (-not (Test-Path $vlinkStartMenuDir)) {
    New-Item -ItemType Directory -Path $vlinkStartMenuDir -Force | Out-Null
}

$startMenuShortcutPath = Join-Path $vlinkStartMenuDir "$AppName.lnk"
$startShortcut = $wshShell.CreateShortcut($startMenuShortcutPath)
$startShortcut.TargetPath = $targetExe
$startShortcut.WorkingDirectory = $InstallDir
$startShortcut.Description = "V-Link Secure Desktop Messenger"
if (Test-Path $targetIco) {
    $startShortcut.IconLocation = "$targetIco,0"
}
$startShortcut.Save()
Write-Log "Start Menu shortcut created with V-Link icon at: $startMenuShortcutPath" "SUCCESS"

# Step 5: Write Registry Keys
try {
    $regPath = "HKCU:\Software\$AppName"
    if (-not (Test-Path $regPath)) {
        New-Item -Path $regPath -Force | Out-Null
    }
    Set-ItemProperty -Path $regPath -Name "Version" -Value $AppVersion
    Set-ItemProperty -Path $regPath -Name "InstallPath" -Value $InstallDir
    Set-ItemProperty -Path $regPath -Name "Publisher" -Value $Publisher
} catch {
    Write-Log "Note: Registry update skipped ($($_.Exception.Message))" "WARN"
}

Write-Log "==========================================================" "SUCCESS"
Write-Log "  Installation Completed Successfully!                   " "SUCCESS"
Write-Log "==========================================================" "SUCCESS"
