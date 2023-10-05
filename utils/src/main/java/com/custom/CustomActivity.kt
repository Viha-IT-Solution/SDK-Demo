package com.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.R
import com.adsutils.AdsManager
import com.adsutils.GetData
import com.dialogs.SplashDialog


open class CustomActivity(var versionCode: Int = 100000) : AppCompatActivity() {


    var nextActivityIntent: Intent? = null
    lateinit var onFetchDataListener: OnFetchDataListener

    interface OnFetchDataListener {
        fun onDataLoadSuccess()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )


        } catch (e: Exception) {
        }

        val clas = this.localClassName.split(".")
        val className = clas[clas.size - 1]


        if (!isNetworkAvailable()) {
            showNoNetworkDialog()
            return
        }


        if (className == "SplashActivity") {
            GetData(this, versionCode).init(object : GetData.SetOnDataLoadListener {
                override fun onDataLoad() {
                    GetData.adsManager = AdsManager(applicationContext, this@CustomActivity)
                    onFetchDataListener.onDataLoadSuccess()
                }
            })
        } else {
            if (GetData.adsManager!!.customInterstitialNext == GetData.adsManager!!.customInterstitialCount && GetData.adsManager!!.customInterstitialNext != 1) {
                GetData.adsManager!!.customInterstitialCount = 1
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(
                    this@CustomActivity,
                    Uri.parse(GetData.adsManager!!.customInterstitialUrl)
                )
            } else {
                GetData.adsManager!!.customInterstitialCount++
            }
        }


    }

    private fun showNoNetworkDialog() {
        val noNetworkAlertDialog = SplashDialog(this)
        noNetworkAlertDialog.dialogTitle = getString(R.string.no_network_title)
        noNetworkAlertDialog.dialogMessage = getString(R.string.no_network_text)
        noNetworkAlertDialog.dialogButtonText = "Okay"
        noNetworkAlertDialog.setBtnClick(object : SplashDialog.OnDialogButtonClickListener {
            override fun onClick(v: View?) {
                finishAffinity()
            }
        })
        noNetworkAlertDialog.show()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            } else {
                return false
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun setAdsToAdapter(wrapped: RecyclerView.Adapter<*>): RecyclerView.Adapter<*>? {
        return GetData.adsManager?.setNativeAdsToList(wrapped)
    }

    fun setSmallAdsToAdapter(wrapped: RecyclerView.Adapter<*>): RecyclerView.Adapter<*>? {
        return GetData.adsManager?.setSmallNativeAdsToList(wrapped)
    }

    fun startAdsActivity(intent: Intent?) {
        if (!GetData.adsManager!!.getData.isAdsOn()) {
            startActivity(intent)
            return
        }

        GetData.adsManager!!.isBackInterstitial = false
        nextActivityIntent = intent


        var activityFlag = false
        var flag = false
        if (com.ironsource.mediationsdk.IronSource.isInterstitialReady()) {
            flag = true
        } else {
            flag = false
            if (!activityFlag) {
                startActivity(nextActivityIntent)
                activityFlag = true
            } else {
                activityFlag = false
            }
        }


        GetData.adsManager!!.setOnInterstitialCloseListener(object :
            AdsManager.OnInterstitialCloseListener {
            override fun onInterstitialClose(b: Boolean) {
                if (flag) {
                    startActivity(nextActivityIntent)
                }
                GetData.adsManager!!.loadInterstitial(this@CustomActivity)
            }

            override fun onInterstitialCustomShow() {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(
                    this@CustomActivity,
                    Uri.parse(GetData.adsManager!!.customInterstitialUrl)
                )
            }
        })

        if (GetData.adsManager!!.interstitialNextClickCounter == GetData.adsManager!!.totalInterstitialNext) {
            GetData.adsManager!!.showInterstitial(this)
            GetData.adsManager!!.interstitialNextClickCounter = 1
        } else {
            GetData.adsManager!!.interstitialNextClickCounter++


            if (!activityFlag) {
                startActivity(nextActivityIntent)
                activityFlag = true
            }
            return
        }


    }

    fun showBannerAds(bannerAdLayout: LinearLayout) {
        GetData.adsManager?.showBannerAds(bannerAdLayout)
    }

    fun showNativeAds(nativeAdLayout: LinearLayout) {
        GetData.adsManager?.showNativeAds(nativeAdLayout)
    }

    fun showNativeSmallAds(smallNativeAdLayout: LinearLayout) {
        GetData.adsManager?.showNativeSmallAds(smallNativeAdLayout)
    }

    fun showRewardAds(
        currentActivity: Activity,
        onRewardedCloseListener: AdsManager.OnRewardAdsListener
    ) {
        GetData.adsManager?.showRewardAds(currentActivity, onRewardedCloseListener)
    }

    override fun onBackPressed() {
        if (GetData.adsManager!!.isInterstitialOnBack) {

            if (GetData.adsManager!!.interstitialBackClickCounter == GetData.adsManager!!.totalInterstitialBack) {
                GetData.adsManager!!.setBackPressedAds(
                    this,
                    object : AdsManager.OnInterstitialBackCloseListener {
                        override fun onInterstitialClose() {
                            GetData.adsManager!!.loadInterstitial(this@CustomActivity)
                            superOnBackPressed()
                        }
                    })

                GetData.adsManager!!.interstitialBackClickCounter = 1
            } else {
                GetData.adsManager!!.interstitialBackClickCounter++
                superOnBackPressed()
            }
        } else {
            superOnBackPressed()
        }
    }

    fun superOnBackPressed() {
        try {
            onBackPressedDispatcher.onBackPressed()
        } catch (e: NullPointerException) {
            finish()
        }
    }


    override fun onPause() {
        super.onPause()

        val clas = this.localClassName.split(".")
        val className = clas[clas.size - 1]

        if (className == "SplashActivity") {
            return
        }
        try {
            if (GetData.adsManager!!.isIrnSrcBannerLoaded && (GetData.adsManager!!.ironSourceBannerLayout != null)) {
                com.ironsource.mediationsdk.IronSource.destroyBanner(GetData.adsManager!!.ironSourceBannerLayout)
                GetData.adsManager!!.isIrnSrcBannerLoaded = false
//                GetData.adsManager!!.irnSrcFlag = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}