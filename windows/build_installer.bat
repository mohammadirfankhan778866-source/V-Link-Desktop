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

REM 2. Prepare dist directory and build native V-Link.exe
echo [1/3] Preparing staging folder and compiling native V-Link.exe launcher...
if not exist "%~dp0..\dist\V-Link" mkdir "%~dp0..\dist\V-Link"
if not exist "%~dp0Output" mkdir "%~dp0Output"

copy /Y "%~dp0version.json" "%~dp0..\dist\V-Link\version.json" >nul

REM Compile VLinkLauncher.cs to V-Link.exe using csc if available
for /f "tokens=*" %%F in ('dir /s /b "%windir%\Microsoft.NET\Framework64\csc.exe" 2^>nul') do (
    set CSC_PATH="%%F"
    goto :found_csc
)
:found_csc

if defined CSC_PATH (
    echo Compiling V-Link.exe with C# compiler: %CSC_PATH%
    %CSC_PATH% /target:winexe /out:"%~dp0..\dist\V-Link\V-Link.exe" /reference:System.Windows.Forms.dll /reference:System.Drawing.dll "%~dp0VLinkLauncher.cs"
) else (
    echo Compiling V-Link.exe with dotnet...
    dotnet publish "%~dp0VLinkLauncher.csproj" -c Release -r win-x64 --self-contained false -o "%~dp0..\dist\V-Link"
)

copy /Y "%~dp0..\dist\V-Link\V-Link.exe" "%~dp0..\dist\V-Link.exe" >nul 2>&1

echo [2/3] Compiling Windows Installer executable via Inno Setup...
%ISCC_PATH% "%~dp0vlink_setup.iss"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Installer build failed! Check errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [3/3] Success!
echo Installer created at: %~dp0Output\V-Link-Setup-1.0.0.exe
echo.
pause
