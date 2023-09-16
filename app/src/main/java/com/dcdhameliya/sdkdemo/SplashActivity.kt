package com.dcdhameliya.sdkdemo

import android.content.Intent
import android.os.Bundle
import com.dcdhameliya.custom.CustomActivity

class SplashActivity : CustomActivity(BuildConfig.VERSION_CODE),
    CustomActivity.OnFetchDataListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        com.facebook.ads.AdSettings.addTestDevice("71fbf155-3748-46fe-851e-6afe597ff583");

        com.facebook.ads.AdSettings.setDataProcessingOptions(arrayOf<String>())
        com.google.android.gms.ads.MobileAds.initialize(
            this
        )
        run {}

        super.onFetchDataListener = this
    }

    override fun onDataLoadSuccess() {
        val i = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}