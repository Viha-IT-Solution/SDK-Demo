package com.dcdhameliya.adsutils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.dcdhameliya.R
import com.dcdhameliya.advs.AdmobNativeAdAdapter
import com.dcdhameliya.advs.CustomBannerAdView
import com.dcdhameliya.advs.CustomNativeAdView
import com.dcdhameliya.advs.FacebookNativeAdView
import com.dcdhameliya.dialogs.RewardedAdsDialog
import java.util.ArrayList

class AdsManager(private var context: Context, var activity: Activity) {

    val ADS_ADMOB = 1
    val ADS_FACEBOOK = 2
    val ADS_IRNSRC = 3
    val ADS_CUSTOM = -1

    var getData: GetData = GetData(context)

    var interstitialNextClickCounter = 1
    var totalInterstitialNext = 3
    var interstitialBackClickCounter = 1
    var totalInterstitialBack = 3
    var nativeAdsPosition = 7

    var customInterstitialNext = 1
    var customInterstitialCount = 1

    var isInterstitialOnBack = false
    var customInterstitialUrl = ""

    init {
        initIronSource()
        loadInterstitial(activity)
        totalInterstitialNext = getData.interstitialClickCount()
        totalInterstitialBack = getData.interstitialBackCount()
        nativeAdsPosition = getData.nativeAdPosition()
        isInterstitialOnBack = getData.isInterstitialOnBack()
        customInterstitialNext = getData.customInterstitialClickCount()
        customInterstitialUrl = getData.customInterstitialId.url
    }

    private fun initIronSource() {
        if (getData.isIrnSrcKeyAvailable()) {

            val advertisingId = com.ironsource.mediationsdk.IronSource.getAdvertiserId(context)

            com.ironsource.mediationsdk.integration.IntegrationHelper.validateIntegration(context)
            com.ironsource.adapters.supersonicads.SupersonicConfig.getConfigObj().clientSideCallbacks =
                true
            com.ironsource.mediationsdk.IronSource.setUserId(advertisingId)
            com.ironsource.mediationsdk.IronSource.init(context, getData.ironSrcKey)
            com.ironsource.mediationsdk.integration.IntegrationHelper.validateIntegration(context)


        }
    }

    private var admobInterstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd? = null
    private var facebookInterstitialAd: com.facebook.ads.InterstitialAd? = null

    var progressDialog: RewardedAdsDialog? = null

    var nativeAdsFlag: Boolean = false

    // Ads Error Listener
    private var onBannerErrorListener: OnBannerErrorListener? = null
    private var onNativeErrorListener: OnNativeErrorListener? = null
    private var onInterstitialErrorListener: OnInterstitialErrorListener? = null
    private var onRewardedCloseListener: OnRewardAdsListener? = null

    // Interstitial Close listener
    private var onInterstitialCloseListener: OnInterstitialCloseListener? = null

    // Interstitial Back Close listener
    private var onInterstitialBackCloseListener: OnInterstitialBackCloseListener? = null


    //    Banner Ads Show
    fun showBannerAds(bannerAdViewLayout: LinearLayout): AdsManager {
        val adsPriority = getData.getAdsPriority()
        val priorityCount = 0

        if (getData.isAdsOn()) {
            showBannerAdsBasedOnPriority(bannerAdViewLayout, adsPriority, priorityCount)
        } else {
            bannerAdViewLayout.visibility = View.GONE
        }
        return this
    }

    private fun showBannerAdsBasedOnPriority(
        bannerAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        showCustomBanner(bannerAdViewLayout)
        if (adsPriority.size == priorityCount) {
            return
        }

        when (adsPriority[priorityCount]) {
            ADS_ADMOB -> {
                showAdmobBanner(bannerAdViewLayout, adsPriority, priorityCount)
            }

            ADS_FACEBOOK -> {
                showFacebookBanner(bannerAdViewLayout, adsPriority, priorityCount)
            }

            ADS_IRNSRC -> {

                if (irnSrcFlag == 0) {
                    irnSrcFlag++
                    showIrnSrcBanner(bannerAdViewLayout, adsPriority, priorityCount)
                } else {
                    showBannerAdsBasedOnPriority(bannerAdViewLayout, adsPriority, priorityCount + 1)
                }

            }

            ADS_CUSTOM -> {
                showCustomBanner(bannerAdViewLayout)
            }

            else -> {
                showCustomBanner(bannerAdViewLayout)
            }
        }

    }

    //    Banner Ads Start
    private fun showAdmobBanner(
        bannerAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        val adTag = getData.admobBannerId

        val bannerAdView = com.google.android.gms.ads.AdView(context)
        bannerAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER)
        bannerAdView.adUnitId = adTag
        val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()
        bannerAdView.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdLoaded() {
                bannerAdViewLayout.removeAllViews()
                bannerAdViewLayout.addView(bannerAdView)
            }

            override fun onAdFailedToLoad(adError: com.google.android.gms.ads.LoadAdError) {
//               Code to show custom or next banner Banner
                showBannerAdsBasedOnPriority(bannerAdViewLayout, adsPriority, priorityCount + 1)
            }
        }
        bannerAdView.loadAd(adRequest)
    }

    private fun showFacebookBanner(
        bannerAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        val adTag = getData.facebookBannerId

        val adView = com.facebook.ads.AdView(
            context, adTag, com.facebook.ads.AdSize.BANNER_HEIGHT_50
        )
        val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {
            override fun onError(ad: com.facebook.ads.Ad, adError: com.facebook.ads.AdError) {
                //               Code to show custom or next banner Banner
                showBannerAdsBasedOnPriority(bannerAdViewLayout, adsPriority, priorityCount + 1)
            }

            override fun onAdLoaded(ad: com.facebook.ads.Ad) {
                bannerAdViewLayout.removeAllViews()
                bannerAdViewLayout.addView(adView)
            }

            override fun onAdClicked(ad: com.facebook.ads.Ad) {}
            override fun onLoggingImpression(ad: com.facebook.ads.Ad) {}
        }
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())
    }

    var irnSrcFlag = 0


    private fun showIrnSrcBanner(
        bannerAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        val size = com.ironsource.mediationsdk.ISBannerSize.BANNER
        val mIronSourceBannerLayout =
            com.ironsource.mediationsdk.IronSource.createBanner(activity, size)

        mIronSourceBannerLayout.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )

        mIronSourceBannerLayout.levelPlayBannerListener =
            object : com.ironsource.mediationsdk.sdk.LevelPlayBannerListener {

                override fun onAdLoaded(adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                    bannerAdViewLayout.removeAllViews()
                    bannerAdViewLayout.addView(mIronSourceBannerLayout)
                    setIrnSrcBannerLoad(mIronSourceBannerLayout)
                }

                override fun onAdLoadFailed(error: com.ironsource.mediationsdk.logger.IronSourceError) {
                    showBannerAdsBasedOnPriority(bannerAdViewLayout, adsPriority, priorityCount + 1)
                }

                override fun onAdClicked(adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                }

                override fun onAdScreenPresented(adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                }

                override fun onAdScreenDismissed(adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                }

                override fun onAdLeftApplication(adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                }
            }
        com.ironsource.mediationsdk.IronSource.loadBanner(mIronSourceBannerLayout)


    }

    private fun showCustomBanner(bannerAdViewLayout: LinearLayout) {
        if (!getData.isCustomAdsShow()) return

        bannerAdViewLayout.removeAllViews()
        val inflater = LayoutInflater.from(context)
        val customNativeView = inflater.inflate(
            R.layout.custom_banner, bannerAdViewLayout, false
        ) as LinearLayout

        val customBannerId = getData.customBannerId
        CustomBannerAdView(context, customNativeView, customBannerId, getData.getNativeBtnConfig())
        bannerAdViewLayout.addView(customNativeView)
    }

    //    Native Ads Show
    fun showNativeAds(nativeAdViewLayout: LinearLayout): AdsManager {
        val adsPriority = getData.getAdsPriority()
        val priorityCount = 0

        if (getData.isAdsOn()) {
            showNativeAdsBasedOnPriority(nativeAdViewLayout, adsPriority, priorityCount)
        } else {
            nativeAdViewLayout.visibility = View.GONE
        }
        return this
    }

    private fun showNativeAdsBasedOnPriority(
        nativeAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        showCustomNative(nativeAdViewLayout)
        if (adsPriority.size == priorityCount) {
            return
        }

        when (adsPriority[priorityCount]) {
            ADS_ADMOB -> {
                showAdmobNative(nativeAdViewLayout, adsPriority, priorityCount)
            }

            ADS_FACEBOOK -> {
                showFacebookNative(nativeAdViewLayout, adsPriority, priorityCount)
            }

            ADS_IRNSRC -> {

                if (irnSrcFlag == 0) {
                    irnSrcFlag++
                    showIrnSrcNative(nativeAdViewLayout, adsPriority, priorityCount)
                } else {
                    showNativeAdsBasedOnPriority(nativeAdViewLayout, adsPriority, priorityCount + 1)
                }
            }

            ADS_CUSTOM -> {
                showCustomNative(nativeAdViewLayout)
            }

            else -> {
                showCustomNative(nativeAdViewLayout)
            }
        }

    }


    //    Small Native Ads Show
    fun showNativeSmallAds(nativeAdViewLayout: LinearLayout): AdsManager {
        val adsPriority = getData.getAdsPriority()
        val priorityCount = 0

        if (getData.isAdsOn()) {
            showNativeSmallAdsBasedOnPriority(nativeAdViewLayout, adsPriority, priorityCount)
        } else {
            nativeAdViewLayout.visibility = View.GONE
        }
        return this
    }

    private fun showNativeSmallAdsBasedOnPriority(
        nativeAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        showCustomSmallNative(nativeAdViewLayout)
        if (adsPriority.size == priorityCount) {
            return
        }

        when (adsPriority[priorityCount]) {
            ADS_ADMOB -> {
                showAdmobSmallNative(nativeAdViewLayout, adsPriority, priorityCount)
            }

            ADS_FACEBOOK -> {
                showFacebookSmallNative(nativeAdViewLayout, adsPriority, priorityCount)
            }

            ADS_IRNSRC -> {
                if (irnSrcFlag == 0) {
                    irnSrcFlag++
                    showIrnSrcSmallNative(nativeAdViewLayout, adsPriority, priorityCount)
                } else {
                    showNativeSmallAdsBasedOnPriority(
                        nativeAdViewLayout, adsPriority, priorityCount + 1
                    )
                }
            }

            ADS_CUSTOM -> {
                showCustomSmallNative(nativeAdViewLayout)
            }

            else -> {
                showCustomSmallNative(nativeAdViewLayout)
            }
        }

    }


    //  Native Ads Start
    private fun showAdmobNative(
        nativeAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {
        val inflater = LayoutInflater.from(context)
        val nativeAdView = inflater.inflate(R.layout.admob_native, null, false)

        val adTag = getData.admobNativeId

        val builder =
            com.google.android.gms.ads.AdLoader.Builder(context, adTag).forNativeAd { nativeAd ->
                val template: com.dcdhameliya.advs.AdmobNativeAdView =
                    nativeAdView.findViewById(R.id.my_template)
                template.setButtonData(getData.getNativeBtnConfig())
                template.setNativeAd(nativeAd)
            }.withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdLoaded() {
                    nativeAdViewLayout.removeAllViews()
                    nativeAdViewLayout.addView(nativeAdView)
                }

                override fun onAdFailedToLoad(adError: com.google.android.gms.ads.LoadAdError) {

//                 Code to show custom or next Native

                    showNativeAdsBasedOnPriority(nativeAdViewLayout, adsPriority, priorityCount + 1)


                }
            })
        val adLoader: com.google.android.gms.ads.AdLoader = builder.build()
        adLoader.loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
    }


    private fun showFacebookNative(
        nativeAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        val inflater = LayoutInflater.from(context)
        val nativeAdView = inflater.inflate(
            R.layout.facebook_native, nativeAdViewLayout, false
        ) as com.facebook.ads.NativeAdLayout


        val adTag = getData.facebookNativeId


        val nativeAd = com.facebook.ads.NativeAd(context, adTag)

        val nativeAdListener: com.facebook.ads.NativeAdListener =
            object : com.facebook.ads.NativeAdListener {
                override fun onMediaDownloaded(ad: com.facebook.ads.Ad) {}
                override fun onError(ad: com.facebook.ads.Ad, adError: com.facebook.ads.AdError) {
//                    New code for custom or next native
                    showNativeAdsBasedOnPriority(nativeAdViewLayout, adsPriority, priorityCount + 1)
                }

                override fun onAdLoaded(ad: com.facebook.ads.Ad) {
                    if (nativeAd == null || nativeAd !== ad) {
                        return
                    }

                    nativeAdViewLayout.removeAllViews()
                    nativeAdViewLayout.addView(nativeAdView)
                    FacebookNativeAdView(
                        context, nativeAd, getData.getNativeBtnConfig(), nativeAdView
                    )
                }

                override fun onAdClicked(ad: com.facebook.ads.Ad) {}
                override fun onLoggingImpression(ad: com.facebook.ads.Ad) {}
            }

        nativeAd.loadAd(
            nativeAd.buildLoadAdConfig()
                .withMediaCacheFlag(com.facebook.ads.NativeAdBase.MediaCacheFlag.ALL)
                .withAdListener(nativeAdListener).build()
        )

    }


    private fun showIrnSrcNative(
        nativeAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        val size = com.ironsource.mediationsdk.ISBannerSize.RECTANGLE
        val mIronSourceBannerLayout =
            com.ironsource.mediationsdk.IronSource.createBanner(activity, size)

        mIronSourceBannerLayout.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )

        mIronSourceBannerLayout.levelPlayBannerListener =
            object : com.ironsource.mediationsdk.sdk.LevelPlayBannerListener {

                override fun onAdLoaded(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                    nativeAdViewLayout.removeAllViews()
                    nativeAdViewLayout.addView(mIronSourceBannerLayout)
                    setIrnSrcBannerLoad(mIronSourceBannerLayout)

                }

                override fun onAdLoadFailed(p0: com.ironsource.mediationsdk.logger.IronSourceError?) {
                    showNativeAdsBasedOnPriority(nativeAdViewLayout, adsPriority, priorityCount + 1)
                }

                override fun onAdClicked(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}

                override fun onAdLeftApplication(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}

                override fun onAdScreenPresented(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}

                override fun onAdScreenDismissed(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}
            }

        com.ironsource.mediationsdk.IronSource.loadBanner(mIronSourceBannerLayout)

    }

    private fun showCustomNative(nativeAdViewLayout: LinearLayout) {
        if (!getData.isCustomAdsShow()) return

        nativeAdViewLayout.removeAllViews()
        val inflater = LayoutInflater.from(context)
        val customNativeView = inflater.inflate(
            R.layout.custom_native, nativeAdViewLayout, false
        ) as LinearLayout

        val customNativeId = getData.customNativeId
        CustomNativeAdView(context, customNativeView, customNativeId, getData.getNativeBtnConfig())
        nativeAdViewLayout.addView(customNativeView)

    }

//    Native Ads End

//    Small Native Ads Start

    fun showAdmobSmallNative(
        nativeAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {
        val inflater = LayoutInflater.from(context)
        val nativeAdView = inflater.inflate(R.layout.admob_small_native, null, false)

        val adTag = getData.admobNativeId

        val builder =
            com.google.android.gms.ads.AdLoader.Builder(context, adTag).forNativeAd { nativeAd ->
                val template: com.dcdhameliya.advs.SmallAdmobNativeAdView =
                    nativeAdView.findViewById(R.id.my_template)
                template.setButtonData(getData.getNativeBtnConfig())
                template.setNativeAd(nativeAd)
            }.withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdLoaded() {
                    nativeAdViewLayout.removeAllViews()
                    nativeAdViewLayout.addView(nativeAdView)
                }

                override fun onAdFailedToLoad(adError: com.google.android.gms.ads.LoadAdError) {
//                 Code to show custom or next Native

                    showNativeSmallAdsBasedOnPriority(
                        nativeAdViewLayout, adsPriority, priorityCount + 1
                    )
                }
            })
        val adLoader: com.google.android.gms.ads.AdLoader = builder.build()
        adLoader.loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
    }


    fun showFacebookSmallNative(
        nativeAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        val inflater = LayoutInflater.from(context)
        val nativeAdView = inflater.inflate(
            R.layout.facebook_small_native, nativeAdViewLayout, false
        ) as com.facebook.ads.NativeAdLayout

        val adTag = getData.facebookNativeId

        val nativeAd = com.facebook.ads.NativeAd(context, adTag)

        val nativeAdListener: com.facebook.ads.NativeAdListener =
            object : com.facebook.ads.NativeAdListener {
                override fun onMediaDownloaded(ad: com.facebook.ads.Ad) {}
                override fun onError(ad: com.facebook.ads.Ad, adError: com.facebook.ads.AdError) {
                    showNativeSmallAdsBasedOnPriority(
                        nativeAdViewLayout, adsPriority, priorityCount + 1
                    )
                }

                override fun onAdLoaded(ad: com.facebook.ads.Ad) {
                    if (nativeAd == null || nativeAd !== ad) {
                        return
                    }

                    nativeAdViewLayout.removeAllViews()
                    nativeAdViewLayout.addView(nativeAdView)
                    FacebookNativeAdView(
                        context, nativeAd, getData.getNativeBtnConfig(), nativeAdView
                    )
                }

                override fun onAdClicked(ad: com.facebook.ads.Ad) {}
                override fun onLoggingImpression(ad: com.facebook.ads.Ad) {}
            }

        nativeAd.loadAd(
            nativeAd.buildLoadAdConfig()
                .withMediaCacheFlag(com.facebook.ads.NativeAdBase.MediaCacheFlag.ALL)
                .withAdListener(nativeAdListener).build()
        )

    }


    private fun showIrnSrcSmallNative(
        nativeAdViewLayout: LinearLayout, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {
        val size = com.ironsource.mediationsdk.ISBannerSize.LARGE
        val mIronSourceBannerLayout =
            com.ironsource.mediationsdk.IronSource.createBanner(activity, size)


        mIronSourceBannerLayout.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )

        mIronSourceBannerLayout.levelPlayBannerListener =
            object : com.ironsource.mediationsdk.sdk.LevelPlayBannerListener {

                override fun onAdLoaded(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                    nativeAdViewLayout.removeAllViews()
                    nativeAdViewLayout.addView(mIronSourceBannerLayout)
                    setIrnSrcBannerLoad(mIronSourceBannerLayout)
                }

                override fun onAdLoadFailed(error: com.ironsource.mediationsdk.logger.IronSourceError) {
                    showNativeSmallAdsBasedOnPriority(
                        nativeAdViewLayout, adsPriority, priorityCount + 1
                    )
                }

                override fun onAdClicked(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}

                override fun onAdLeftApplication(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}

                override fun onAdScreenPresented(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}

                override fun onAdScreenDismissed(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}
            }

        com.ironsource.mediationsdk.IronSource.loadBanner(mIronSourceBannerLayout)

    }


    fun showCustomSmallNative(nativeAdViewLayout: LinearLayout) {
        if (!getData.isCustomAdsShow()) return

        nativeAdViewLayout.removeAllViews()
        val inflater = LayoutInflater.from(context)
        val customNativeView = inflater.inflate(
            R.layout.custom_small_native, nativeAdViewLayout, false
        ) as LinearLayout

        val customNativeId = getData.customNativeId
        CustomNativeAdView(context, customNativeView, customNativeId, getData.getNativeBtnConfig())
        nativeAdViewLayout.addView(customNativeView)

    }

    //    Small Native Ads End


//    Interstitial Ads Start

    fun loadInterstitial(context: Context) {

        val adsPriority = getData.getAdsPriority()
        val priorityCount = 0

        if (getData.isAdsOn()) {
            loadInterstitialAdsBasedOnPriority(context, adsPriority, priorityCount)
        }
    }


    private fun loadInterstitialAdsBasedOnPriority(
        context: Context, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {
        if (adsPriority.size == priorityCount) {
            return
        }

        when (adsPriority[priorityCount]) {
            ADS_ADMOB -> {
                loadAdmobInterstitialAds(context, adsPriority, priorityCount)
            }

            ADS_FACEBOOK -> {
                loadFacebookInterstitialAds(context, adsPriority, priorityCount)
            }

            ADS_IRNSRC -> {
                loadIrnSrcInterstitialAds(adsPriority, priorityCount)
            }

        }

    }

    var LOADED_ADS = 0

    private fun loadAdmobInterstitialAds(
        context: Context, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        val adTag = getData.admobInterstitialId

        com.google.android.gms.ads.interstitial.InterstitialAd.load(context,
            adTag,
            com.google.android.gms.ads.AdRequest.Builder().build(),
            object : com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: com.google.android.gms.ads.LoadAdError) {
                    admobInterstitialAd = null
                    loadInterstitialAdsBasedOnPriority(context, adsPriority, priorityCount + 1)
                }

                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                    admobInterstitialAd = interstitialAd

                    if (admobInterstitialAd != null) {
                        LOADED_ADS = ADS_ADMOB
                        admobInterstitialAd?.fullScreenContentCallback =
                            object : com.google.android.gms.ads.FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    onInterstitialClose()
                                }

                                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                                    onInterstitialClose()
                                }

                                override fun onAdShowedFullScreenContent() {
                                    admobInterstitialAd = null
                                }
                            }
                    }
                }
            })
    }

    private fun loadFacebookInterstitialAds(
        context: Context, adsPriority: ArrayList<Int>, priorityCount: Int
    ) {

        val adTag = getData.facebookInterstitialId

        facebookInterstitialAd = com.facebook.ads.InterstitialAd(context, adTag)

        val interstitialAdListener: com.facebook.ads.InterstitialAdListener =
            object : com.facebook.ads.InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: com.facebook.ads.Ad) {}

                override fun onInterstitialDismissed(ad: com.facebook.ads.Ad) {
                    onInterstitialClose()
                }

                override fun onError(ad: com.facebook.ads.Ad, adError: com.facebook.ads.AdError) {
                    loadInterstitialAdsBasedOnPriority(context, adsPriority, priorityCount + 1)
                }

                override fun onAdLoaded(ad: com.facebook.ads.Ad) {
                    LOADED_ADS = ADS_FACEBOOK
                }

                override fun onAdClicked(ad: com.facebook.ads.Ad) {}
                override fun onLoggingImpression(ad: com.facebook.ads.Ad) {}
            }

        facebookInterstitialAd!!.loadAd(
            facebookInterstitialAd!!.buildLoadAdConfig().withAdListener(interstitialAdListener)
                .build()
        )
    }


    private fun loadIrnSrcInterstitialAds(adsPriority: ArrayList<Int>, priorityCount: Int) {


        com.ironsource.mediationsdk.IronSource.setLevelPlayInterstitialListener(object :
            com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener {
            override fun onAdReady(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                LOADED_ADS = ADS_IRNSRC
            }

            override fun onAdLoadFailed(error: com.ironsource.mediationsdk.logger.IronSourceError) {
                loadInterstitialAdsBasedOnPriority(context, adsPriority, priorityCount + 1)
            }

            override fun onAdOpened(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
            }

            override fun onAdShowSucceeded(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
            }

            override fun onAdShowFailed(
                p0: com.ironsource.mediationsdk.logger.IronSourceError?,
                p1: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
            ) {
                LOADED_ADS = 0
            }

            override fun onAdClicked(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
            }

            override fun onAdClosed(p0: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                onInterstitialClose(true)
            }
        })
        com.ironsource.mediationsdk.IronSource.loadInterstitial()
    }


    private fun showCustomInterstitialAds(context: Context) {
        onInterstitialCloseListener!!.onInterstitialCustomShow()
    }

    fun showInterstitial(activity: Context): AdsManager {

        if (getData.isAdsOn()) {
            when (LOADED_ADS) {
                ADS_ADMOB -> {
                    admobInterstitialAd!!.show(activity as Activity)
                }

                ADS_FACEBOOK -> {
                    facebookInterstitialAd!!.show()
                }

                ADS_IRNSRC -> {
                    if (com.ironsource.mediationsdk.IronSource.isInterstitialReady()) {
                        com.ironsource.mediationsdk.IronSource.showInterstitial()
                    } else {
                        showCustomInterstitialAds(context)
                    }
                }

                else -> {
                    showCustomInterstitialAds(context)
                }

            }
        } else {
            onInterstitialClose()
        }
        return this
    }


    fun setOnBannerErrorListener(onBannerErrorListener: OnBannerErrorListener): AdsManager {
        this.onBannerErrorListener = onBannerErrorListener
        return this
    }

    fun setOnNativeErrorListener(onNativeErrorListener: OnNativeErrorListener): AdsManager {
        this.onNativeErrorListener = onNativeErrorListener
        return this
    }

    fun setOnInterstitialErrorListener(onInterstitialErrorListener: OnInterstitialErrorListener): AdsManager {
        this.onInterstitialErrorListener = onInterstitialErrorListener
        return this
    }

    fun setOnInterstitialCloseListener(onInterstitialCloseListener: OnInterstitialCloseListener): AdsManager {
        this.onInterstitialCloseListener = onInterstitialCloseListener
        return this
    }

    interface OnBannerErrorListener {
        fun onBannerError()
    }

    interface OnNativeErrorListener {
        fun onNativeError()
    }

    interface OnInterstitialErrorListener {
        fun onInterstitialError()
    }

    interface OnCustomInterCloseListener {
        fun onClose()
    }

    interface OnInterstitialCloseListener {
        fun onInterstitialClose(b: Boolean)
        fun onInterstitialCustomShow()
    }

    interface OnInterstitialBackCloseListener {
        fun onInterstitialClose()
    }

    interface OnRewardAdsListener {
        fun onRewardEarned()
        fun onRewardedError()
    }

    var isBackInterstitial = false


    fun setBackPressedAds(
        activity: Context, onInterstitialBackCloseListener: OnInterstitialBackCloseListener
    ): AdsManager {
        isBackInterstitial = true
        this.onInterstitialBackCloseListener = onInterstitialBackCloseListener
        if (getData.isInterstitialOnBack()) {
            showInterstitial(activity)
        } else {
            onInterstitialClose()
        }
        onInterstitialClose()
        return this
    }

    fun onInterstitialClose(flag: Boolean = false) {
        if (isBackInterstitial) {
            onInterstitialBackCloseListener!!.onInterstitialClose()
        } else {
            onInterstitialCloseListener!!.onInterstitialClose(false)
        }
    }


//    private val isNetworkConnected: Boolean
//        get() {
//            var result = false
//            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//            cm?.run {
//                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
//                    result = when {
//                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                        else -> false
//                    }
//                }
//            }
//            return result
//        }


    fun showRewardAds(currentActivity: Activity, onRewardedCloseListener: OnRewardAdsListener) {
        this.onRewardedCloseListener = onRewardedCloseListener
        if (getData.isAdsOn()) {
            when (getData.getAdsPriority()[0]) {
                ADS_ADMOB -> {
                    progressDialog = RewardedAdsDialog(currentActivity)
                    progressDialog!!.show()
                    showAdmobRewarded(currentActivity)
                }

                ADS_FACEBOOK -> {
                    progressDialog = RewardedAdsDialog(currentActivity)
                    progressDialog!!.show()
                    showFacebookRewarded(currentActivity)
                }

                ADS_IRNSRC -> {
                    progressDialog = RewardedAdsDialog(currentActivity)
                    progressDialog!!.show()
                    showIrnSrcRewarded(currentActivity)
                }

                else -> {
                    onRewardedCloseListener.onRewardEarned()
                }
            }
        } else {
            onRewardedCloseListener.onRewardEarned()
        }
    }


    private fun showAdmobRewarded(currentActivity: Activity) {
        val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()
        com.google.android.gms.ads.rewarded.RewardedAd.load(context,
            getData.admobRewardedId,
            adRequest,
            object : com.google.android.gms.ads.rewarded.RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                    videoNotAvailable(currentActivity)
                }

                override fun onAdLoaded(rewardedAd: com.google.android.gms.ads.rewarded.RewardedAd) {
                    onAdmobRewardLoad(rewardedAd, currentActivity)
                    progressDialog!!.dismiss()
                }
            })
    }

    fun onAdmobRewardLoad(
        admobRewardedVideoAd: com.google.android.gms.ads.rewarded.RewardedAd,
        currentActivity: Activity
    ) {

        admobRewardedVideoAd.fullScreenContentCallback =
            object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdClicked() {}

                override fun onAdDismissedFullScreenContent() {
                    onRewardedCloseListener!!.onRewardEarned()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    videoNotAvailable(currentActivity)
                }

                override fun onAdImpression() {}

                override fun onAdShowedFullScreenContent() {}

            }

        admobRewardedVideoAd.show(
            currentActivity
        ) {
            onRewardedCloseListener!!.onRewardEarned()
        }

    }


    private fun showFacebookRewarded(currentActivity: Activity) {
        val rewardedVideoAd = com.facebook.ads.RewardedVideoAd(currentActivity, "YOUR_PLACEMENT_ID")
        val rewardedVideoAdListener: com.facebook.ads.RewardedVideoAdListener =
            object : com.facebook.ads.RewardedVideoAdListener {
                override fun onError(ad: com.facebook.ads.Ad?, error: com.facebook.ads.AdError) {
                    videoNotAvailable(currentActivity)
                }

                override fun onAdLoaded(ad: com.facebook.ads.Ad?) {
                    progressDialog!!.dismiss()
                    rewardedVideoAd.show()
                }

                override fun onAdClicked(ad: com.facebook.ads.Ad?) {}

                override fun onLoggingImpression(ad: com.facebook.ads.Ad?) {}

                override fun onRewardedVideoCompleted() {}

                override fun onRewardedVideoClosed() {
                    onRewardedCloseListener!!.onRewardEarned()
                }
            }
        rewardedVideoAd.loadAd(
            rewardedVideoAd.buildLoadAdConfig().withAdListener(rewardedVideoAdListener).build()
        )
    }

    private fun showIrnSrcRewarded(currentActivity: Activity) {


        com.ironsource.mediationsdk.IronSource.setLevelPlayRewardedVideoListener(object :
            com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener {
            override fun onAdAvailable(adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {}

            override fun onAdUnavailable() {}

            override fun onAdOpened(adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                progressDialog!!.dismiss()
            }

            override fun onAdClosed(adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo) {
                onRewardedCloseListener!!.onRewardEarned()
            }

            override fun onAdRewarded(
                placement: com.ironsource.mediationsdk.model.Placement,
                adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
            ) {
            }

            override fun onAdShowFailed(
                error: com.ironsource.mediationsdk.logger.IronSourceError,
                adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
            ) {
                videoNotAvailable(currentActivity)
            }

            override fun onAdClicked(
                placement: com.ironsource.mediationsdk.model.Placement,
                adInfo: com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
            ) {
            }

        })

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                if (com.ironsource.mediationsdk.IronSource.isRewardedVideoAvailable()) {
                    com.ironsource.mediationsdk.IronSource.showRewardedVideo()
                } else {
                    mainHandler.postDelayed(this, 1000)
                }
            }
        })

    }


    fun videoNotAvailable(currentActivity: Activity) {
        progressDialog!!.dismiss()
        onRewardedCloseListener!!.onRewardedError()
        progressDialog = RewardedAdsDialog(currentActivity)
        progressDialog!!.show(true)
    }

    fun setNativeAdsToList(wrapped: RecyclerView.Adapter<*>): RecyclerView.Adapter<*>? {
        return AdmobNativeAdAdapter.Builder.with(
            context,
            getData,
            false,
            wrapped
        ).build()
    }

    fun setSmallNativeAdsToList(wrapped: RecyclerView.Adapter<*>): RecyclerView.Adapter<*>? {
        return AdmobNativeAdAdapter.Builder.with(
            context,
            getData,
            true,
            wrapped
        ).build()
    }

    fun setIrnSrcBannerLoad(
        mIronSourceBannerLayout: com.ironsource.mediationsdk.IronSourceBannerLayout
    ) {
        irnSrcFlag = 0
        isIrnSrcBannerLoaded = true
        ironSourceBannerLayout = mIronSourceBannerLayout

    }

    var isIrnSrcBannerLoaded = false
    var ironSourceBannerLayout: com.ironsource.mediationsdk.IronSourceBannerLayout? = null

}

