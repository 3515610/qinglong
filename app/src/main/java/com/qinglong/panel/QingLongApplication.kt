package com.qinglong.panel

import android.app.Application

class QingLongApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: QingLongApplication
            private set
    }
}
