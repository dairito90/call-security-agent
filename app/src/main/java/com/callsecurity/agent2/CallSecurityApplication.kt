package com.callsecurity.agent2

import android.app.Application
import android.util.Log

class CallSecurityApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("CallSecurityApp", "Application started")
    }
}
