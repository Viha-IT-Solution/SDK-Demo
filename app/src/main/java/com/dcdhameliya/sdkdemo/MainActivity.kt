package com.dcdhameliya.sdkdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.dcdhameliya.adsutils.AdsManager
import com.dcdhameliya.custom.CustomActivity
import com.dcdhameliya.sdkdemo.databinding.ActivityMainBinding

class MainActivity : CustomActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()
        showBannerAds(binding.adView)
        showBannerAds(binding.adViewBanner)
        showBannerAds(binding.adViewMedium)
        showNativeAds(binding.nativeAdView)
        showNativeSmallAds(binding.nativeAdViewSmall)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnInterstitial.setOnClickListener {
            startAdsActivity(Intent(this@MainActivity, NextActivity::class.java))
        }

        binding.btnRewardedVideo.setOnClickListener {
            showRewardAds(this@MainActivity, object : AdsManager.OnRewardAdsListener {

                override fun onRewardEarned() {
                    Toast.makeText(
                        this@MainActivity,
                        "Reward Earned",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onRewardedError() {
                    Toast.makeText(
                        this@MainActivity,
                        "Error in loading ads",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }


    }
}