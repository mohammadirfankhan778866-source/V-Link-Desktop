; Script generated for V-Link Desktop Installer
; Inno Setup 6.x Modern GUI Script

#define MyAppName "V-Link"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "V-Link Messenger Inc."
#define MyAppURL "https://v-link.chat"
#define MyAppExeName "V-Link.exe"

[Setup]
; Unique application GUID
AppId={{9F8214BC-2C1E-4780-99E8-54AC717B8092}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
OutputDir=.\Output
OutputBaseFilename=V-Link-Setup-{#MyAppVersion}
Compression=lzma2/ultra64
SolidCompression=yes
WizardStyle=modern
WizardSizePercent=105
WizardResizable=no
PrivilegesRequired=lowest
DisableProgramGroupPage=no
DisableWelcomePage=no

; Visual UI Branding & Styling
ShowLanguageDialog=auto
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Messages]
WelcomeLabel1=Welcome to the V-Link Desktop Setup Wizard
WelcomeLabel2=This will install %1 version %2 on your computer.%n%nV-Link is an ultra-fast, end-to-end encrypted messaging client designed for Windows desktop.%n%nClick Next to continue, or Cancel to exit Setup.
SelectDirLabel3=Setup will install %1 into the following folder.
ClickNext=Click Next to continue, or click Browse to choose a different installation folder.
FinishedHeadingLabel=Completing the V-Link Setup Wizard
FinishedLabelNoIcons=Setup has finished installing %1 on your computer.%n%nYou can launch the application by clicking the desktop icon.

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "Additional Shortcuts:"; Flags: checkedonce
Name: "quicklaunchicon"; Description: "Create a Quick Launch shortcut"; GroupDescription: "Additional Shortcuts:"; Flags: unchecked

[Files]
; Distributable app files and bundled runtime
Source: "..\dist\V-Link\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; Fallback standalone executable if present
Source: "..\dist\V-Link.exe"; DestDir: "{app}"; Flags: ignoreversion skipifsourcedoesntexist

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{group}\V-Link (Safe Mode)"; Filename: "{app}\{#MyAppExeName}"; Parameters: "--safe-mode"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "Launch V-Link Desktop Messenger"; Flags: nowait postinstall skipifsilent skipifnotsilent
