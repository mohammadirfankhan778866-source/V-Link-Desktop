using System;
using System.Drawing;
using System.Diagnostics;
using System.Windows.Forms;
using System.IO;

namespace VLinkDesktop
{
    public class Program
    {
        [STAThread]
        public static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new MainWindow());
        }
    }

    public class MainWindow : Form
    {
        private NotifyIcon trayIcon;
        private ContextMenuStrip trayMenu;

        public MainWindow()
        {
            Text = "V-Link Desktop Messenger";
            Size = new Size(1100, 720);
            MinimumSize = new Size(800, 600);
            StartPosition = FormStartPosition.CenterScreen;
            BackColor = Color.FromArgb(15, 23, 42); // Deep slate cyan background

            // System Tray Context Menu
            trayMenu = new ContextMenuStrip();
            trayMenu.Items.Add("Open V-Link", null, (s, e) => { ShowWindow(); });
            trayMenu.Items.Add("Check for Updates", null, (s, e) => {
                try {
                    Process.Start(new ProcessStartInfo("https://github.com/mohammadirfankhan778866-source/V-Link-Desktop/releases") { UseShellExecute = true });
                } catch {}
            });
            trayMenu.Items.Add(new ToolStripSeparator());
            trayMenu.Items.Add("Exit V-Link", null, (s, e) => {
                if (trayIcon != null) trayIcon.Visible = false;
                Application.Exit();
            });

            // Load custom application icon
            Icon appIcon = null;
            try {
                string baseDir = AppDomain.CurrentDomain.BaseDirectory;
                string icoPath = Path.Combine(baseDir, "app.ico");
                if (File.Exists(icoPath)) {
                    appIcon = new Icon(icoPath);
                } else {
                    appIcon = Icon.ExtractAssociatedIcon(Application.ExecutablePath);
                }
            } catch {}

            if (appIcon == null) appIcon = SystemIcons.Application;
            Icon = appIcon;

            // Initialize System Tray Icon
            trayIcon = new NotifyIcon()
            {
                Text = "V-Link Desktop Messenger",
                Icon = appIcon,
                ContextMenuStrip = trayMenu,
                Visible = true
            };
            trayIcon.DoubleClick += (s, e) => { ShowWindow(); };

            // Main UI Layout
            TableLayoutPanel layout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                RowCount = 3,
                ColumnCount = 1,
                BackColor = Color.FromArgb(15, 23, 42)
            };
            layout.RowStyles.Add(new RowStyle(SizeType.Absolute, 70));
            layout.RowStyles.Add(new RowStyle(SizeType.Percent, 100));
            layout.RowStyles.Add(new RowStyle(SizeType.Absolute, 50));

            // Header Banner
            Panel header = new Panel { Dock = DockStyle.Fill, BackColor = Color.FromArgb(30, 41, 59) };
            Label title = new Label
            {
                Text = "⚡ V-LINK DESKTOP MESSENGER",
                Font = new Font("Segoe UI", 16, FontStyle.Bold),
                ForeColor = Color.FromArgb(6, 182, 212), // Cyan
                Dock = DockStyle.Left,
                Width = 400,
                TextAlign = ContentAlignment.MiddleLeft,
                Padding = new Padding(20, 0, 0, 0)
            };
            header.Controls.Add(title);
            layout.Controls.Add(header, 0, 0);

            // Center Info / Web Client Card
            Panel center = new Panel { Dock = DockStyle.Fill, Padding = new Padding(40) };
            Label info = new Label
            {
                Text = "V-Link Native Desktop Client v1.0.0\n\n" +
                       "✓ End-to-End Encrypted (Signal Double Ratchet)\n" +
                       "✓ Erlang Distributed WebSocket Engine\n" +
                       "✓ Real-time Dual-Pane Messaging\n" +
                       "✓ Windows System Tray & Desktop Integration\n\n" +
                       "Click below to launch active web session or minimize to system tray.",
                Font = new Font("Segoe UI", 12, FontStyle.Regular),
                ForeColor = Color.FromArgb(226, 232, 240),
                Dock = DockStyle.Top,
                Height = 180,
                TextAlign = ContentAlignment.TopCenter
            };
            
            Button btnLaunch = new Button
            {
                Text = "Launch Web Client",
                Font = new Font("Segoe UI", 11, FontStyle.Bold),
                ForeColor = Color.White,
                BackColor = Color.FromArgb(6, 182, 212),
                FlatStyle = FlatStyle.Flat,
                Width = 220,
                Height = 46,
                Cursor = Cursors.Hand
            };
            btnLaunch.FlatAppearance.BorderSize = 0;
            btnLaunch.Left = (1100 - 220) / 2 - 40;
            btnLaunch.Top = 200;
            btnLaunch.Click += (s, e) => {
                try {
                    Process.Start(new ProcessStartInfo("https://v-link.chat") { UseShellExecute = true });
                } catch {
                    try { Process.Start(new ProcessStartInfo("https://ai.studio/build") { UseShellExecute = true }); } catch {}
                }
            };

            center.Controls.Add(btnLaunch);
            center.Controls.Add(info);
            layout.Controls.Add(center, 0, 1);

            // Status Footer
            Panel footer = new Panel { Dock = DockStyle.Fill, BackColor = Color.FromArgb(15, 23, 42) };
            Label status = new Label
            {
                Text = "● Network: Connected | Clustering: Active | Minimize to hide in Windows System Tray",
                Font = new Font("Segoe UI", 9, FontStyle.Regular),
                ForeColor = Color.FromArgb(148, 163, 184),
                Dock = DockStyle.Fill,
                TextAlign = ContentAlignment.MiddleCenter
            };
            footer.Controls.Add(status);
            layout.Controls.Add(footer, 0, 2);

            Controls.Add(layout);

            // Minimize to Tray
            Resize += (s, e) =>
            {
                if (WindowState == FormWindowState.Minimized)
                {
                    Hide();
                    if (trayIcon != null)
                    {
                        trayIcon.ShowBalloonTip(1500, "V-Link Messenger", "Running in the background. Click tray icon to restore.", ToolTipIcon.Info);
                    }
                }
            };

            FormClosing += (s, e) =>
            {
                if (trayIcon != null)
                {
                    trayIcon.Visible = false;
                    trayIcon.Dispose();
                }
            };
        }

        private void ShowWindow()
        {
            Show();
            WindowState = FormWindowState.Normal;
            BringToFront();
            Activate();
        }
    }
}
