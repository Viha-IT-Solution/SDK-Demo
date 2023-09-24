package com.viha.sdkdemo

import android.app.Application
import com.adsutils.AppOpenManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppOpenManager(this)
    }
}