package com.messenger.mobile

import android.app.Application
import com.messenger.mobile.data.AppContainer

class MessengerApp : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}