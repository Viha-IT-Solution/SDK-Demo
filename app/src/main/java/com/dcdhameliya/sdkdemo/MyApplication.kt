package com.dcdhameliya.sdkdemo

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        com.dcdhameliya.adsutils.AppOpenManager(this)
    }
}