package com.dcdhameliya.adsutils

import android.app.Application.ActivityLifecycleCallbacks
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import android.app.Activity
import android.app.Application
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest

class AppOpenManager(private val myApplication: Application) : ActivityLifecycleCallbacks,
    LifecycleObserver {
    private var appOpenAd: AppOpenAd? = null
    private lateinit var loadCallback: AppOpenAdLoadCallback
    private var currentActivity: Activity? = null
    fun fetchAd() {
        if (isAdAvailable) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                this@AppOpenManager.appOpenAd = appOpenAd
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }
        }
        val request = adRequest
        if (GetData.isAdmobAppopenAvailable()) AppOpenAd.load(
            myApplication,
            GetData.admobRandomAppOpenId,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            loadCallback
        )
    }

    fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd!!.show(currentActivity!!)
        } else {
            fetchAd()
        }
    }

    private val adRequest: AdRequest
        private get() = AdRequest.Builder().build()
    val isAdAvailable: Boolean
        get() = appOpenAd != null

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfAvailable()
    }

    companion object {
        var isShowingAd = false
    }

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
}