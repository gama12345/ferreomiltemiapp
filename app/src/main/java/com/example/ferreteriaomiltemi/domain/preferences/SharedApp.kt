package com.example.ferreteriaomiltemi.domain.preferences

import android.app.Application

class SharedApp : Application() {
    companion object {
        lateinit var prefs: PrefsOrderCart
    }
    override fun onCreate() {
        super.onCreate()
        prefs = PrefsOrderCart(applicationContext)
    }
}