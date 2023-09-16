package com.dcdhameliya.adsutils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.view.View
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dcdhameliya.R
import com.dcdhameliya.dialogs.SplashDialog
import com.dcdhameliya.model.CustomBanner
import com.dcdhameliya.model.CustomInterstitial
import com.dcdhameliya.model.CustomNative
import com.dcdhameliya.model.Device
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

class GetData {

    var context: Context
    private var versionCode: Int = 0

    constructor(context: Context) {
        this.context = context
    }

    constructor(context: Context, versionCode: Int) {
        this.context = context
        this.versionCode = versionCode
    }

    interface SetOnDataLoadListener {
        fun onDataLoad()
    }

    lateinit var listener: SetOnDataLoadListener;

    fun init(listener: SetOnDataLoadListener) {
        this.listener = listener
        val build = Build();

        val device = Device()
        device.DEVICE_ID =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        device.SESSION_START_TIME = Calendar.getInstance().time.toString()
        device.DEVICE = Build.DEVICE
        device.BRAND = Build.BRAND
        device.DISPLAY = Build.DISPLAY
        device.FINGERPRINT = Build.FINGERPRINT
        device.HARDWARE = Build.HARDWARE
        device.HOST = Build.HOST
        device.ID = Build.ID
        device.MANUFACTURER = Build.MANUFACTURER
        device.MODEL = Build.MODEL
        device.PRODUCT = Build.PRODUCT
        device.SERIAL = Build.SERIAL
        device.TAGS = Build.TAGS
        device.TIME = Build.TIME.toString()
        device.TYPE = Build.TYPE
        device.UNKNOWN = Build.UNKNOWN
        device.USER = Build.USER


        val data: ByteArray = Base64.decode(context.getString(R.string.firebase_id), Base64.DEFAULT)

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Method.GET,
            String(data, StandardCharsets.UTF_8),
            Response.Listener { response ->
                setResponseData(JSONObject(response))

            },
            Response.ErrorListener { }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["APPLICATION"] = context.packageName.toString()
                headers["DEVICE"] = Gson().toJson(device)
                return headers
            }
        }
        queue.add(stringRequest)
    }

    private fun setResponseData(jsonObject: JSONObject) {

        if (jsonObject.optBoolean("MAINTENANCE", true)) {
            showMaintenanceDialog()
            return
        }

        if (checkForUpdate(jsonObject.getJSONObject("ADS_CONFIG"))) {
            return
        }

        ADS_CONFIG = jsonObject.getJSONObject("ADS_CONFIG").getJSONObject("ads")
        MORE_APPS = jsonObject.getJSONArray("MORE_APPS")
        POLICY = jsonObject.getString("POLICY")
        EXTRA = jsonObject.getJSONObject("EXTRA")

        listener.onDataLoad()

    }

    var flag = 0
    private fun callListener() {
        flag++

        if (flag == 2) listener.onDataLoad()
    }


    private fun checkForUpdate(jsonObject: JSONObject): Boolean {
        if (versionCode < jsonObject.optInt("version_code", versionCode)) {
            showUpdateDialog(jsonObject.optString("package", context.packageName))
            return true
        }
        return false
    }

    private fun showUpdateDialog(pkgName: String?) {
        val updateAlertDialog = SplashDialog(context as Activity)
        updateAlertDialog.dialogTitle = context.getString(R.string.update_title)
        updateAlertDialog.dialogMessage = context.getString(R.string.update_text)
        updateAlertDialog.dialogButtonText = "Update"
        updateAlertDialog.setBtnClick(object : SplashDialog.OnDialogButtonClickListener {
            override fun onClick(v: View?) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$pkgName")
                    )
                )
            }
        })
        updateAlertDialog.show()
    }

    private fun showMaintenanceDialog() {
        val maintenanceAlertDialog = SplashDialog(context as Activity)
        maintenanceAlertDialog.dialogTitle = context.getString(R.string.maintenance_title)
        maintenanceAlertDialog.dialogMessage = context.getString(R.string.maintenance_text)
        maintenanceAlertDialog.dialogButtonText = "Okay"
        maintenanceAlertDialog.setBtnClick(object : SplashDialog.OnDialogButtonClickListener {
            override fun onClick(v: View?) {
                (context as Activity).finish()
            }
        })
        maintenanceAlertDialog.show()
    }

    fun isAdsOn(): Boolean {
        return ADS_CONFIG!!.optBoolean("is_ads_on", false)
    }

    fun getAdsPriority(): ArrayList<Int> {
        val priority = ArrayList<Int>()
        val temp = ADS_CONFIG!!.optString("ads_priority", "-1").split(",")
        for (i in temp) {
            if (i.isEmpty()) continue

            priority.add(i.toInt())
        }
        return priority
    }

    fun isCustomAdsShow(): Boolean {
        return ADS_CONFIG!!.optBoolean("is_custom_ads_show", false)
    }

    fun interstitialClickCount(): Int {
        return ADS_CONFIG!!.optInt("interstitial_click_count", 3)
    }

    fun isInterstitialOnBack(): Boolean {
        return ADS_CONFIG!!.optBoolean("is_interstitial_on_back", false)
    }

    fun interstitialBackCount(): Int {
        return ADS_CONFIG!!.optInt("interstitial_back_count", 3)
    }

    fun customInterstitialClickCount(): Int {

        if (!ADS_CONFIG!!.optBoolean("is_custom_interstitial", true)) {
            return 1
        }
        val ads = getAdsPriority()
        if (ads[0] == -1) {
            return 1
        }
        return ADS_CONFIG!!.optInt("custom_interstitial_count", 1)
    }

    fun nativeAdPosition(): Int {
        return ADS_CONFIG!!.optInt("native_ad_position", 4)
    }

    fun getNativeBtnConfig(): JSONObject {
        return ADS_CONFIG!!.getJSONObject("native_btn_config")
    }


    fun isPriority(): Boolean {
        return ADS_CONFIG!!.optBoolean("is_priority", false)
    }

    //  ad tags available
    fun isAdmobBannerAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("admob_banner").optBoolean("status", false))
    }

    fun isAdmobInterstitialAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("admob_interstitial").optBoolean("status", false))
    }

    fun isAdmobNativeAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("admob_native").optBoolean("status", false))
    }

    fun isAdmobRewardedAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("admob_rewarded").optBoolean("status", false))

    }

    fun isFacebookBannerAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("facebook_banner").optBoolean("status", false))
    }

    fun isFacebookInterstitialAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("facebook_interstitial").optBoolean("status", false))
    }

    fun isFacebookNativeAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("facebook_native").optBoolean("status", false))
    }

    fun isFacebookRewardedAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("facebook_rewarded").optBoolean("status", false))
    }

    fun isIrnSrcKeyAvailable(): Boolean {
        return (ADS_CONFIG!!.getJSONObject("ironsrc_key").optBoolean("status"))
    }


    // fetch random id....   Admob
    val admobBannerId: String
        get() {
            val bannerIds = ArrayList<String>()
            val temp = ADS_CONFIG!!.getJSONObject("admob_banner").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }
    val admobInterstitialId: String
        get() {
            val bannerIds = ArrayList<String>()
            val temp = ADS_CONFIG!!.getJSONObject("admob_interstitial").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }
    val admobNativeId: String
        get() {
            val bannerIds = ArrayList<String>()

            val temp = ADS_CONFIG!!.getJSONObject("admob_native").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }
    val admobRewardedId: String
        get() {
            val bannerIds = ArrayList<String>()
            val temp = ADS_CONFIG!!.getJSONObject("admob_rewarded").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }


    // fetch random id....  Facebook
    val facebookBannerId: String
        get() {
            val bannerIds = ArrayList<String>()
            val temp = ADS_CONFIG!!.getJSONObject("facebook_banner").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }
    val facebookInterstitialId: String
        get() {
            val bannerIds = ArrayList<String>()
            val temp = ADS_CONFIG!!.getJSONObject("facebook_interstitial").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }
    val facebookNativeId: String
        get() {
            val bannerIds = ArrayList<String>()

            val temp = ADS_CONFIG!!.getJSONObject("facebook_native").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }
    val facebookRewardedId: String
        get() {
            val bannerIds = ArrayList<String>()
            val temp = ADS_CONFIG!!.getJSONObject("facebook_rewarded").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }


    //fetch IRN SRC KEY
    val ironSrcKey: String
        get() {
            val bannerIds = ArrayList<String>()
            val temp = ADS_CONFIG!!.getJSONObject("ironsrc_key").getJSONArray("id")
            for (i in 0 until temp.length()) {
                bannerIds.add(temp.getString(i))
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }


    // fetch random id....  Custom
    val customBannerId: CustomBanner
        get() {
            val bannerIds = ArrayList<CustomBanner>()
            val jsonArray = ADS_CONFIG!!.getJSONObject("custom_banner").getJSONArray("id")
            for (i in 0 until jsonArray.length()) {
                val tmp = jsonArray.getJSONObject(i)
                bannerIds.add(
                    CustomBanner(
                        tmp.getString("title"),
                        tmp.getString("body"),
                        tmp.getString("icon"),
                        tmp.getString("image"),
                        tmp.getString("url"),
                        tmp.optString("btn_text", "Play")
                    )
                )
            }
            val rand = Random()
            return bannerIds[rand.nextInt(bannerIds.size)]
        }
    val customNativeId: CustomNative
        get() {
            val nativeIds = ArrayList<CustomNative>()
            val jsonArray = ADS_CONFIG!!.getJSONObject("custom_native").getJSONArray("id")
            for (i in 0 until jsonArray.length()) {
                val tmp = jsonArray.getJSONObject(i)
                nativeIds.add(
                    CustomNative(
                        tmp.getString("title"),
                        tmp.getString("body"),
                        tmp.getString("icon"),
                        tmp.getString("image"),
                        tmp.getString("url"),
                        tmp.optString("btn_text", "Play Now")
                    )
                )
            }
            val rand = Random()
            return nativeIds[rand.nextInt(nativeIds.size)]
        }
    val customInterstitialId: CustomInterstitial
        get() {
            val interstitialIds = ArrayList<CustomInterstitial>()
            val jsonArray = ADS_CONFIG!!.getJSONObject("custom_interstitial").getJSONArray("id")
            for (i in 0 until jsonArray.length()) {
                val tmp = jsonArray.getJSONObject(i)
                interstitialIds.add(
                    CustomInterstitial(
                        tmp.getString("url")
                    )
                )
            }
            val rand = Random()
            return interstitialIds[rand.nextInt(interstitialIds.size)]
        }


    companion object {
        var ADS_CONFIG: JSONObject? = null

        lateinit var MORE_APPS: JSONArray
        lateinit var EXTRA: JSONObject

        var POLICY: String = ""

        var adsManager: AdsManager? = null

        fun isAdmobAppopenAvailable(): Boolean {
            if (ADS_CONFIG == null) {
                return false
            }

            return if (ADS_CONFIG!!.optBoolean("is_ads_on", false)) {
                ADS_CONFIG!!.getJSONObject("admob_appopen").optBoolean("status")
            } else false
        }

        val admobRandomAppOpenId: String
            get() {
                val bannerIds = ArrayList<String>()
                val temp = ADS_CONFIG!!.getJSONObject("admob_appopen").getJSONArray("id")
                for (i in 0 until temp.length()) {
                    bannerIds.add(temp.getString(i))
                }
                val rand = Random()
                return bannerIds[rand.nextInt(bannerIds.size)]
            }
    }
}