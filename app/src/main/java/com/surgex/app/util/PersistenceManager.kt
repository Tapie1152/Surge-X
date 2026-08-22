package com.surgex.app.util

import android.content.Context
import android.content.SharedPreferences
import com.surgex.app.ui.navigation.SurgeXScreen

class PersistenceManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        "surgex_persistence",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val LAST_SCREEN_KEY = "last_screen"
        private const val LAST_POSITION_KEY = "last_position"
        private const val USER_PROFILE_PIC_KEY = "user_profile_pic"
        private const val USER_PROFILE_PIC_UPLOADED_KEY = "user_profile_pic_uploaded"
        private const val LAST_SCROLL_POSITION_KEY = "last_scroll_position"
    }

    // Save last screen
    fun saveLastScreen(screen: String) {
        preferences.edit().putString(LAST_SCREEN_KEY, screen).apply()
    }

    // Get last screen
    fun getLastScreen(): String? {
        return preferences.getString(LAST_SCREEN_KEY, null)
    }

    // Save scroll position
    fun saveScrollPosition(key: String, position: Int) {
        preferences.edit().putInt(key, position).apply()
    }

    // Get scroll position
    fun getScrollPosition(key: String): Int {
        return preferences.getInt(key, 0)
    }

    // Save profile picture URI
    fun saveProfilePicUri(uri: String) {
        preferences.edit().putString(USER_PROFILE_PIC_KEY, uri).apply()
    }

    // Get profile picture URI
    fun getProfilePicUri(): String? {
        return preferences.getString(USER_PROFILE_PIC_KEY, null)
    }

    // Mark profile pic as uploaded
    fun setProfilePicUploaded(uploaded: Boolean) {
        preferences.edit().putBoolean(USER_PROFILE_PIC_UPLOADED_KEY, uploaded).apply()
    }

    // Check if profile pic is uploaded
    fun isProfilePicUploaded(): Boolean {
        return preferences.getBoolean(USER_PROFILE_PIC_UPLOADED_KEY, false)
    }
}
