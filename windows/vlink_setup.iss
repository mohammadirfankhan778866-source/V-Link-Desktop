; Script generated for V-Link Desktop Installer
; Inno Setup 6.x Script

#define MyAppName "V-Link"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "V-Link Messenger"
#define MyAppURL "https://ai.studio/build"
#define MyAppExeName "V-Link.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
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
SetupIconFile=..\app\src\main\res\drawable\pulse_chat_icon.png
Compression=lzma2/max
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=lowest
DisableProgramGroupPage=auto

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 6.1; Check: not IsAdminInstallMode

[Files]
; Distributable app files (Package directory from Compose Desktop or Java runtime)
Source: "..\dist\V-Link\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; Fallback standalone executable if compiled via single bundle
Source: "..\dist\V-Link.exe"; DestDir: "{app}"; Flags: ignoreversion skipifsourcedoesntexist

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\{#MyAppExeName}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon; IconFilename: "{app}\{#MyAppExeName}"

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent
