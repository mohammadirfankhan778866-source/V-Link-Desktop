package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdminDashboardSheet
import com.example.ui.components.CallActiveOverlay
import com.example.ui.components.NavigationTabItemData
import com.example.ui.theme.PulseGreen
import com.example.ui.theme.VLinkCyan
import com.example.ui.viewmodels.MainViewModel
import com.example.ui.viewmodels.NavigationTab

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val activeChatId by viewModel.activeChatId.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isCallActiveScreenOpen by viewModel.isCallActiveScreenOpen.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val isAdminDashboardOpen by viewModel.isAdminDashboardOpen.collectAsState()

    // Desktop States
    val isMinimizedToTray by viewModel.isMinimizedToTray.collectAsState()
    val isTrayMenuOpen by viewModel.isTrayMenuOpen.collectAsState()
    val isWindowMaximized by viewModel.isWindowMaximized.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()
    val updateInfo by viewModel.appUpdateInfo.collectAsState()
    val showKeyboardShortcutsDialog by viewModel.showKeyboardShortcutsDialog.collectAsState()

    var showNewChatModal by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavigationTabItemData(
            tab = NavigationTab.CHATS,
            label = "Chats",
            selectedIcon = Icons.Default.Chat,
            unselectedIcon = Icons.Outlined.Chat
        ),
        NavigationTabItemData(
            tab = NavigationTab.UPDATES,
            label = "Updates",
            selectedIcon = Icons.Default.DonutLarge,
            unselectedIcon = Icons.Outlined.DonutLarge
        ),
        NavigationTabItemData(
            tab = NavigationTab.POSTS,
            label = "Posts",
            selectedIcon = Icons.Default.RssFeed,
            unselectedIcon = Icons.Outlined.RssFeed
        ),
        NavigationTabItemData(
            tab = NavigationTab.CALLS,
            label = "Calls",
            selectedIcon = Icons.Default.Call,
            unselectedIcon = Icons.Outlined.Call
        ),
        NavigationTabItemData(
            tab = NavigationTab.CHANNELS,
            label = "Channels",
            selectedIcon = Icons.Default.Language,
            unselectedIcon = Icons.Outlined.Language
        )
    )

    if (!isLoggedIn) {
        val context = androidx.compose.ui.platform.LocalContext.current
        AuthScreen(
            viewModel = viewModel,
            onGoogleSignIn = { email, name, avatar ->
                viewModel.performGoogleLogin(context, email, name, avatar)
            }
        )
    } else if (isMinimizedToTray) {
        // Windows System Tray Background Execution State Widget
        WindowsSystemTrayWidget(
            viewModel = viewModel,
            onRestoreWindow = { viewModel.setMinimizedToTray(false) }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isWindowMaximized) 0.dp else 2.dp)
        ) {
            // Native Desktop Window Title Bar (Windows 11 / Native PC style)
            DesktopWindowTitleBar(
                title = "V-Link Desktop - Encrypted PC Messenger",
                viewModel = viewModel,
                onNewChat = { showNewChatModal = true },
                onAdminDashboard = { viewModel.toggleAdminDashboard(true) },
                onMinimize = { viewModel.setMinimizedToTray(true) },
                onMaximize = { viewModel.toggleWindowMaximized() },
                onClose = { viewModel.handleDesktopWindowClose() },
                onToggleTrayMenu = { viewModel.toggleTrayMenu(!isTrayMenuOpen) }
            )

            // Quick Desktop Shortcut Quick-Action Header Bar
            DesktopQuickShortcutsBar(
                viewModel = viewModel,
                onNewChat = { showNewChatModal = true },
                onOpenShortcuts = { viewModel.toggleKeyboardShortcutsDialog(true) }
            )

            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val isWideDesktop = maxWidth >= 600.dp

                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Desktop Sidebar Navigation Rail
                    NavigationRail(
                        modifier = Modifier.width(if (isWideDesktop) 80.dp else 68.dp).fillMaxHeight(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        header = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = VLinkCyan.copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ElectricBolt,
                                            contentDescription = "V-Link",
                                            tint = VLinkCyan,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        navItems.forEach { item ->
                            val isSelected = currentTab == item.tab
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = { Text(item.label, fontSize = 10.sp) },
                                modifier = Modifier.testTag("desktop_rail_${item.tab.name.lowercase()}")
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // System Tray Quick Trigger
                        IconButton(
                            onClick = { viewModel.toggleTrayMenu(true) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = "Windows System Tray",
                                tint = VLinkCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        NavigationRailItem(
                            selected = currentTab == NavigationTab.SETTINGS,
                            onClick = { viewModel.selectTab(NavigationTab.SETTINGS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == NavigationTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = { Text("Settings", fontSize = 10.sp) },
                            modifier = Modifier.testTag("desktop_rail_settings")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Desktop Workspace Area
                    if (currentTab == NavigationTab.CHATS) {
                        if (isWideDesktop) {
                            // Desktop Dual-Pane (Chat List on Left, Chat Detail / Empty State on Right)
                            Row(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                Box(
                                    modifier = Modifier
                                        .width(340.dp)
                                        .fillMaxHeight()
                                ) {
                                    ChatsScreen(
                                        viewModel = viewModel,
                                        onOpenNewChatModal = { showNewChatModal = true }
                                    )
                                }

                                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    if (activeChatId != null) {
                                        ChatDetailScreen(
                                            chatId = activeChatId!!,
                                            viewModel = viewModel,
                                            onBack = { viewModel.closeChatDetail() }
                                        )
                                    } else {
                                        DesktopEmptyChatPlaceholder(
                                            onStartChat = { showNewChatModal = true }
                                        )
                                    }
                                }
                            }
                        } else {
                            // Single Pane View on Compact Desktop Window
                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                if (activeChatId != null) {
                                    ChatDetailScreen(
                                        chatId = activeChatId!!,
                                        viewModel = viewModel,
                                        onBack = { viewModel.closeChatDetail() }
                                    )
                                } else {
                                    ChatsScreen(
                                        viewModel = viewModel,
                                        onOpenNewChatModal = { showNewChatModal = true }
                                    )
                                }
                            }
                        }
                    } else {
                        // Desktop Single Pane for Status, Posts, Calls, Channels, Settings
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            when (currentTab) {
                                NavigationTab.UPDATES -> StatusScreen(viewModel = viewModel)
                                NavigationTab.POSTS -> PostsScreen(viewModel = viewModel)
                                NavigationTab.CALLS -> CallsScreen(viewModel = viewModel)
                                NavigationTab.CHANNELS -> ChannelsScreen(viewModel = viewModel)
                                NavigationTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    // Windows System Tray Quick Menu Dropdown / Popup
    if (isTrayMenuOpen) {
        WindowsSystemTrayMenuDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleTrayMenu(false) }
        )
    }

    // Remote Version Update Dialog
    if (showUpdateDialog && updateInfo != null) {
        DesktopUpdateDialog(
            updateInfo = updateInfo!!,
            onDismiss = { viewModel.dismissUpdateDialog() }
        )
    }

    // Desktop Keyboard Shortcuts Cheat Sheet Dialog
    if (showKeyboardShortcutsDialog) {
        DesktopKeyboardShortcutsDialog(
            onDismiss = { viewModel.toggleKeyboardShortcutsDialog(false) }
        )
    }

    // New Chat / New Group Modal
    if (showNewChatModal) {
        NewChatModal(
            viewModel = viewModel,
            onDismiss = { showNewChatModal = false }
        )
    }

    // Active Voice/Video Call Overlay
    if (isCallActiveScreenOpen && activeCall != null) {
        CallActiveOverlay(
            call = activeCall!!,
            viewModel = viewModel,
            onEndCall = { viewModel.endCall() }
        )
    }

    // Admin Dashboard Sheet
    if (isAdminDashboardOpen) {
        AdminDashboardSheet(
            onDismiss = { viewModel.toggleAdminDashboard(false) }
        )
    }
}

@Composable
fun DesktopWindowTitleBar(
    title: String,
    viewModel: MainViewModel,
    onNewChat: () -> Unit,
    onAdminDashboard: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onClose: () -> Unit,
    onToggleTrayMenu: () -> Unit
) {
    val updateInfo by viewModel.appUpdateInfo.collectAsState()
    val isWindowMaximized by viewModel.isWindowMaximized.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon & title
            Surface(
                shape = CircleShape,
                color = VLinkCyan.copy(alpha = 0.15f),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = VLinkCyan,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Desktop Menus: File, Edit, Cluster, Shortcuts
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onNewChat,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text("New Chat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(
                    onClick = onAdminDashboard,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text("Erlang Cluster", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(
                    onClick = { viewModel.checkForUpdates(isUserInitiated = true) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Updates", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (updateInfo?.hasUpdate == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFFEF4444), CircleShape))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Windows System Tray Icon Button
            IconButton(
                onClick = onToggleTrayMenu,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Inbox,
                    contentDescription = "Tray Menu",
                    tint = VLinkCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Node Connection Badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF10B981).copy(alpha = 0.15f),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Encrypted Node",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF10B981)
                    )
                }
            }

            // Desktop Window Control Buttons (Minimize, Maximize, Close)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                // Minimize Button
                Surface(
                    onClick = onMinimize,
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Remove, contentDescription = "Minimize to Tray", modifier = Modifier.size(14.dp))
                    }
                }

                // Maximize / Restore Button
                Surface(
                    onClick = onMaximize,
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (isWindowMaximized) Icons.Default.FilterNone else Icons.Default.CropSquare,
                            contentDescription = "Maximize Window",
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Close Button (Red Accent)
                Surface(
                    onClick = onClose,
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.2f),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = "Close to Tray", tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DesktopQuickShortcutsBar(
    viewModel: MainViewModel,
    onNewChat: () -> Unit,
    onOpenShortcuts: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        modifier = Modifier.fillMaxWidth().height(30.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.clickable { onNewChat() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ctrl+N", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VLinkCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Chat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(
                    modifier = Modifier.clickable { viewModel.cycleToNextChat() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ctrl+Tab", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VLinkCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Next Chat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(
                    modifier = Modifier.clickable { viewModel.selectTab(NavigationTab.SETTINGS) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ctrl+,", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VLinkCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Settings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(
                modifier = Modifier.clickable { onOpenShortcuts() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Keyboard, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("All Desktop Shortcuts (Ctrl+/)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun WindowsSystemTrayWidget(
    viewModel: MainViewModel,
    onRestoreWindow: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.widthIn(max = 480.dp).padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = VLinkCyan.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = VLinkCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = "V-Link Running in Windows System Tray",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "V-Link is active in the background and will alert you with desktop toasts for incoming messages and encrypted calls.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onRestoreWindow,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = VLinkCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Window", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.checkForUpdates(isUserInitiated = true) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Check Updates", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WindowsSystemTrayMenuDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val desktopNotifications by viewModel.desktopNotifications.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = VLinkCyan.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = VLinkCyan, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Windows System Tray", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Quick actions for V-Link Desktop background service:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    onClick = {
                        viewModel.setMinimizedToTray(false)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = VLinkCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Open Main Window", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Bring V-Link window to foreground", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Surface(
                    onClick = {
                        viewModel.checkForUpdates(isUserInitiated = true)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Check for Updates", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Poll remote endpoint for latest release", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Surface(
                    onClick = {
                        viewModel.setDesktopNotifications(!desktopNotifications)
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (desktopNotifications) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (desktopNotifications) PulseGreen else Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (desktopNotifications) "Mute Desktop Notifications" else "Unmute Desktop Notifications",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (desktopNotifications) "Currently receiving toast banners" else "Notifications currently silenced",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    onClick = {
                        viewModel.setMinimizedToTray(true)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Minimize, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Minimize to Tray", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Keep running silently in taskbar", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close Menu", color = VLinkCyan, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DesktopUpdateDialog(
    updateInfo: com.example.data.network.AppUpdateInfo,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = VLinkCyan, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("V-Link Desktop Update", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Version ${updateInfo.latestVersion} Available", fontSize = 12.sp, color = VLinkCyan)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current: v${updateInfo.currentVersion}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("New Build: v${updateInfo.latestVersion}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                        Text("Size: ${updateInfo.fileSizeMb}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text("Release Highlights & Changes:", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    updateInfo.releaseNotes.forEach { note ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text("• ", color = VLinkCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(note, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Installer is bundled with Windows Setup Wizard (Inno Setup) and replaces existing installation safely.",
                        fontSize = 11.sp,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(updateInfo.downloadUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.util.Log.e("DesktopUpdateDialog", "Could not open download URL: ${e.message}")
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = VLinkCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Download Update (.exe)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Remind Me Later", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun DesktopKeyboardShortcutsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Keyboard, contentDescription = null, tint = VLinkCyan, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Desktop Keyboard Shortcuts", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val shortcuts = listOf(
                    "Ctrl + N" to "Start New Direct Chat or Group",
                    "Ctrl + Tab" to "Cycle to Next Chat Thread",
                    "Ctrl + Shift + Tab" to "Cycle to Previous Chat Thread",
                    "Ctrl + 1 .. 5" to "Switch Sidebar Tabs (Chats, Updates, Posts, Calls, Channels)",
                    "Ctrl + ," to "Open Desktop Settings Preferences",
                    "Ctrl + M" to "Maximize / Restore Application Window",
                    "Ctrl + W / Esc" to "Close Active Conversation / Minimize to Tray",
                    "Ctrl + U" to "Check for Application Version Updates",
                    "Ctrl + /" to "Show this Keyboard Shortcuts Guide"
                )

                shortcuts.forEach { (keys, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = keys,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = VLinkCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VLinkCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Got It", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DesktopEmptyChatPlaceholder(
    onStartChat: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.widthIn(max = 440.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = VLinkCyan.copy(alpha = 0.12f),
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = VLinkCyan,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "V-Link for Desktop",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select a conversation from the left to start messaging, or start a new encrypted direct chat or group.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onStartChat,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VLinkCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Conversation", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "End-to-end encrypted with Signal Protocol",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun RowScope.NavigationTabItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label
            )
        },
        label = { Text(label) }
    )
}
