package com.bryan.platform.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.bryan.platform.PlatformApplication;

/**
 * 会话管理器，负责管理用户登录状态和Token
 */
public class SessionManager {

    private static final String PREFS_NAME = "platform_app_prefs";
    private static final String KEY_AUTH_TOKEN   = "auth_token";
    private static final String KEY_USERNAME     = "username";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    @SuppressWarnings("StaticFieldLeak")
    private volatile static SessionManager instance;

    private final SharedPreferences sharedPref;

    private SessionManager() {
        sharedPref = PlatformApplication.applicationContext()
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    /**
     * 保存登录信息和Token
     */
    public void saveAuthToken(String token, String username, @Nullable String refreshToken) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.putString(KEY_USERNAME, username);
        if (refreshToken != null) {
            editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        }
        editor.apply();
    }

    public void saveAuthToken(String token, String username) {
        saveAuthToken(token, username, null);
    }

    /**
     * 获取当前Token
     */
    @Nullable
    public String fetchAuthToken() {
        return sharedPref.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * 获取刷新Token
     */
    @Nullable
    public String fetchRefreshToken() {
        return sharedPref.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * 获取当前用户名
     */
    @Nullable
    public String fetchUsername() {
        return sharedPref.getString(KEY_USERNAME, null);
    }

    /**
     * 清除登录信息
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.apply();
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return fetchAuthToken() != null;
    }
}