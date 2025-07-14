package com.bryan.platform

import android.app.Application
import android.content.Context

class PlatformApplication : Application() {

    companion object {
        private var instance: PlatformApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}