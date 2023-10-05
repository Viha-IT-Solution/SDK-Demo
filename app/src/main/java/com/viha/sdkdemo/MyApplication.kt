package com.viha.sdkdemo

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        com.adsutils.AppOpenManager(this)
    }
}