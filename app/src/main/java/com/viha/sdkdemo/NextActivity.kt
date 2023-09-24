package com.viha.sdkdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.custom.CustomActivity

class NextActivity : CustomActivity() {


    override fun onStart() {
        super.onStart()
        showBannerAds(findViewById<LinearLayout>(R.id.adView_banner))
        showNativeAds(findViewById<LinearLayout>(R.id.adView))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        val btnInterstitial = findViewById<Button>(R.id.btnInterstitial)
        btnInterstitial.setOnClickListener {
            startAdsActivity(Intent(this@NextActivity, NextToNextActivity::class.java))
        }

    }
}