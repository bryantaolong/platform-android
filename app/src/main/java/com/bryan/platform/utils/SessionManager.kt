package com.bryan.platform.utils

import android.content.Context
import android.content.SharedPreferences
import com.bryan.platform.PlatformApplication

/**
 * 会话管理器，负责管理用户登录状态和Token
 */
class SessionManager private constructor() {

    companion object {
        private const val PREFS_NAME = "platform_app_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager().also { instance = it }
            }
        }
    }

    private val sharedPref: SharedPreferences by lazy {
        PlatformApplication.applicationContext().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    /**
     * 保存登录信息和Token
     */
    fun saveAuthToken(token: String, username: String, refreshToken: String? = null) {
        with(sharedPref.edit()) {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USERNAME, username)
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            apply()
        }
    }

    /**
     * 获取当前Token
     */
    fun fetchAuthToken(): String? {
        return sharedPref.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * 获取刷新Token
     */
    fun fetchRefreshToken(): String? {
        return sharedPref.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * 获取当前用户名
     */
    fun fetchUsername(): String? {
        return sharedPref.getString(KEY_USERNAME, null)
    }

    /**
     * 清除登录信息
     */
    fun clear() {
        with(sharedPref.edit()) {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_USERNAME)
            remove(KEY_REFRESH_TOKEN)
            apply()
        }
    }

    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        return fetchAuthToken() != null
    }
}