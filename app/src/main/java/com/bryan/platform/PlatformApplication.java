package com.bryan.platform;

import android.app.Application;
import android.content.Context;

public class PlatformApplication extends Application {

    private static PlatformApplication instance;

    public static Context applicationContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}