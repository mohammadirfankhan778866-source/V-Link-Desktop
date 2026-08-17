package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.UserEntity
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pulse_chat_session", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean(KEY_IS_LOGGED_IN, false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _jwtToken = MutableStateFlow<String?>(
        if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) prefs.getString(KEY_JWT_TOKEN, null) else null
    )
    val jwtToken: StateFlow<String?> = _jwtToken

    private val _themeMode = MutableStateFlow(
        AppThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name)
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode

    private val _showExactTimestamps = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_EXACT_TIMESTAMPS, true)
    )
    val showExactTimestamps: StateFlow<Boolean> = _showExactTimestamps

    private val _chatWallpaper = MutableStateFlow(
        prefs.getString(KEY_CHAT_WALLPAPER, "DEFAULT") ?: "DEFAULT"
    )
    val chatWallpaper: StateFlow<String> = _chatWallpaper

    // Desktop Specific Preferences
    private val _startOnBoot = MutableStateFlow(prefs.getBoolean(KEY_START_ON_BOOT, true))
    val startOnBoot: StateFlow<Boolean> = _startOnBoot

    private val _closeToTray = MutableStateFlow(prefs.getBoolean(KEY_CLOSE_TO_TRAY, true))
    val closeToTray: StateFlow<Boolean> = _closeToTray

    private val _minimizeToTray = MutableStateFlow(prefs.getBoolean(KEY_MINIMIZE_TO_TRAY, false))
    val minimizeToTray: StateFlow<Boolean> = _minimizeToTray

    private val _desktopNotifications = MutableStateFlow(prefs.getBoolean(KEY_DESKTOP_NOTIFICATIONS, true))
    val desktopNotifications: StateFlow<Boolean> = _desktopNotifications

    private val _notificationSound = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATION_SOUND, true))
    val notificationSound: StateFlow<Boolean> = _notificationSound

    private val _notificationPreview = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATION_PREVIEW, true))
    val notificationPreview: StateFlow<Boolean> = _notificationPreview

    private val _autoCheckUpdates = MutableStateFlow(prefs.getBoolean(KEY_AUTO_CHECK_UPDATES, true))
    val autoCheckUpdates: StateFlow<Boolean> = _autoCheckUpdates

    private val _hardwareAcceleration = MutableStateFlow(prefs.getBoolean(KEY_HARDWARE_ACCELERATION, true))
    val hardwareAcceleration: StateFlow<Boolean> = _hardwareAcceleration

    fun performGoogleSignIn(
        email: String = "mohammadirfankhan778866@gmail.com",
        displayName: String = "Mohammad Irfan Khan",
        avatarUrl: String = "https://picsum.photos/seed/irfan/300/300"
    ): UserEntity {
        val userId = "usr_google_irfan_9075"
        val token = generateJwt(email)
        val username = "@" + email.substringBefore("@").lowercase()

        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_JWT_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, displayName)
            .apply()

        _jwtToken.value = token
        _isLoggedIn.value = true

        return UserEntity(
            id = userId,
            displayName = displayName,
            username = username,
            email = email,
            profilePictureUrl = avatarUrl,
            bio = "Hey there! I am using V-Link ⚡",
            onlineStatus = "ONLINE",
            lastSeenTimestamp = System.currentTimeMillis(),
            accountCreatedDate = "2026-01-15",
            isCurrentUser = true
        )
    }

    fun saveCustomUserSession(token: String, user: UserEntity) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_JWT_TOKEN, token)
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_NAME, user.displayName)
            .apply()

        _jwtToken.value = token
        _isLoggedIn.value = true
    }

    fun updateUserName(displayName: String) {
        prefs.edit().putString(KEY_USER_NAME, displayName).apply()
    }

    fun getCurrentUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_JWT_TOKEN)
            .remove(KEY_USER_ID)
            .apply()
        _isLoggedIn.value = false
        _jwtToken.value = null
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setShowExactTimestamps(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_EXACT_TIMESTAMPS, show).apply()
        _showExactTimestamps.value = show
    }

    fun setChatWallpaper(wallpaper: String) {
        prefs.edit().putString(KEY_CHAT_WALLPAPER, wallpaper).apply()
        _chatWallpaper.value = wallpaper
    }

    fun setStartOnBoot(enable: Boolean) {
        prefs.edit().putBoolean(KEY_START_ON_BOOT, enable).apply()
        _startOnBoot.value = enable
    }

    fun setCloseToTray(enable: Boolean) {
        prefs.edit().putBoolean(KEY_CLOSE_TO_TRAY, enable).apply()
        _closeToTray.value = enable
    }

    fun setMinimizeToTray(enable: Boolean) {
        prefs.edit().putBoolean(KEY_MINIMIZE_TO_TRAY, enable).apply()
        _minimizeToTray.value = enable
    }

    fun setDesktopNotifications(enable: Boolean) {
        prefs.edit().putBoolean(KEY_DESKTOP_NOTIFICATIONS, enable).apply()
        _desktopNotifications.value = enable
    }

    fun setNotificationSound(enable: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_SOUND, enable).apply()
        _notificationSound.value = enable
    }

    fun setNotificationPreview(enable: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_PREVIEW, enable).apply()
        _notificationPreview.value = enable
    }

    fun setAutoCheckUpdates(enable: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_CHECK_UPDATES, enable).apply()
        _autoCheckUpdates.value = enable
    }

    fun setHardwareAcceleration(enable: Boolean) {
        prefs.edit().putBoolean(KEY_HARDWARE_ACCELERATION, enable).apply()
        _hardwareAcceleration.value = enable
    }

    private fun generateJwt(email: String): String {
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val payload = "eyJzdWIiOiIke2VtYWlsfSIsImF1ZCI6InB1bHNlY2hhdF9hcHAiLCJpYXQiOjE3NDIwMDAwMDB9"
        val signature = UUID.randomUUID().toString().replace("-", "").take(16)
        return "$header.$payload.$signature"
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_SHOW_EXACT_TIMESTAMPS = "show_exact_timestamps"
        private const val KEY_CHAT_WALLPAPER = "chat_wallpaper"
        private const val KEY_START_ON_BOOT = "start_on_boot"
        private const val KEY_CLOSE_TO_TRAY = "close_to_tray"
        private const val KEY_MINIMIZE_TO_TRAY = "minimize_to_tray"
        private const val KEY_DESKTOP_NOTIFICATIONS = "desktop_notifications"
        private const val KEY_NOTIFICATION_SOUND = "notification_sound"
        private const val KEY_NOTIFICATION_PREVIEW = "notification_preview"
        private const val KEY_AUTO_CHECK_UPDATES = "auto_check_updates"
        private const val KEY_HARDWARE_ACCELERATION = "hardware_acceleration"
    }
}
