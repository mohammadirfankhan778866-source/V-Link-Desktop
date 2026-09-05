; =========================================================================
;  V-Link Desktop Messenger - NSIS Installer Script (Nullsoft Scriptable Install System)
;  Creates a professional, self-contained Windows installer (.exe) with silent installation support
; =========================================================================

!define PRODUCT_NAME "V-Link"
!define PRODUCT_VERSION "1.0.0"
!define PRODUCT_PUBLISHER "V-Link Messenger Inc."
!define PRODUCT_WEB_SITE "https://github.com/mohammadirfankhan778866-source/V-Link-Desktop"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\V-Link.exe"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

; Modern UI 2
!include "MUI2.nsh"
!include "FileFunc.nsh"
!include "LogicLib.nsh"

; General Configuration
Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "Output\V-Link-NSIS-Setup-${PRODUCT_VERSION}.exe"
InstallDir "$PROGRAMFILES64\V-Link"
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""
ShowInstDetails show
ShowUnInstDetails show
RequestExecutionLevel admin
SetCompressor /SOLID lzma

; UI Configuration
!define MUI_ABORTWARNING

; Pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!define MUI_FINISHPAGE_RUN "$INSTDIR\V-Link.exe"
!define MUI_FINISHPAGE_RUN_NOTCHECKED
!define MUI_FINISHPAGE_RUN_TEXT "Launch V-Link Desktop Messenger"
!insertmacro MUI_PAGE_FINISH

; Uninstaller Pages
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

; Language
!insertmacro MUI_LANGUAGE "English"

; Installer Section
Section "MainSection" SEC01
  SetOutPath "$INSTDIR"
  SetOverwrite ifnewer

  ; Write distribution files and version manifest
  File /r "..\dist\V-Link\*.*"
  File /nonfatal "..\windows\version.json"

  ; Create Start Menu Shortcuts
  CreateDirectory "$SMPROGRAMS\V-Link"
  CreateShortCut "$SMPROGRAMS\V-Link\V-Link.lnk" "$INSTDIR\V-Link.exe"
  CreateShortCut "$SMPROGRAMS\V-Link\Uninstall.lnk" "$INSTDIR\uninst.exe"

  ; Create Desktop Shortcut
  CreateShortCut "$DESKTOP\V-Link.lnk" "$INSTDIR\V-Link.exe"
SectionEnd

Section -Post
  ; Write uninstaller
  WriteUninstaller "$INSTDIR\uninst.exe"

  ; Write Registry Keys for Windows Add/Remove Programs (Apps & Features)
  WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR\V-Link.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" '"$INSTDIR\uninst.exe"'
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "QuietUninstallString" '"$INSTDIR\uninst.exe" /S'
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\V-Link.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
  WriteRegDWORD ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "NoModify" 1
  WriteRegDWORD ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "NoRepair" 1
SectionEnd

; Uninstaller Section
Section Uninstall
  ; Remove shortcuts
  Delete "$DESKTOP\V-Link.lnk"
  Delete "$SMPROGRAMS\V-Link\V-Link.lnk"
  Delete "$SMPROGRAMS\V-Link\Uninstall.lnk"
  RMDir "$SMPROGRAMS\V-Link"

  ; Remove application files
  RMDir /r "$INSTDIR"

  ; Remove registry keys
  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  DeleteRegKey HKLM "${PRODUCT_DIR_REGKEY}"
  SetAutoClose true
SectionEnd
