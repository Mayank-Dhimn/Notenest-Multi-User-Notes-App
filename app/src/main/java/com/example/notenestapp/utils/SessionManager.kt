package com.example.notenestapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("NoteNestPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_USER_NAME = "userName"
    }

    fun saveSession(userId: Int, email: String, name: String) {
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_NAME, name)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, "")

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.commit()
    }
}
