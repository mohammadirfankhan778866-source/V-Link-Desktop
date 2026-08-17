@echo off
REM =========================================================================
REM  V-Link Desktop Windows Installer Builder Script
REM =========================================================================

echo.
echo ===================================================
echo     V-Link Desktop - Windows Installer Builder
echo ===================================================
echo.

REM 1. Check if Inno Setup Compiler (ISCC) exists in standard Program Files locations
set ISCC_PATH=""
if exist "%ProgramFiles(x86)%\Inno Setup 6\ISCC.exe" (
    set ISCC_PATH="%ProgramFiles(x86)%\Inno Setup 6\ISCC.exe"
) else if exist "%ProgramFiles%\Inno Setup 6\ISCC.exe" (
    set ISCC_PATH="%ProgramFiles%\Inno Setup 6\ISCC.exe"
) else if exist "%LOCALAPPDATA%\Programs\Inno Setup 6\ISCC.exe" (
    set ISCC_PATH="%LOCALAPPDATA%\Programs\Inno Setup 6\ISCC.exe"
)

if %ISCC_PATH%=="" (
    echo [ERROR] Inno Setup 6 compiler (ISCC.exe) was not found.
    echo Please download and install Inno Setup 6 from: https://jrsoftware.org/isdl.php
    echo After installing, rerun this script.
    pause
    exit /b 1
)

echo [1/2] Compiling Windows Installer executable via Inno Setup...
%ISCC_PATH% "%~dp0vlink_setup.iss"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Installer build failed! Check errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [2/2] Success!
echo Installer created at: %~dp0Output\V-Link-Setup-1.0.0.exe
echo.
pause
