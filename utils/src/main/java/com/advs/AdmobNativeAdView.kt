package com.advs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.R
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import org.json.JSONObject

class AdmobNativeAdView : FrameLayout {

    private lateinit var nativeAd: NativeAd

    private lateinit var nativeAdView: NativeAdView
    private lateinit var adLlBackground: LinearLayout
    private lateinit var adIconView: ImageView
    private lateinit var adTitle: TextView
    private lateinit var adAdvertiser: TextView
    private lateinit var adStars: RatingBar
    private lateinit var adPrice: TextView
    private lateinit var adStore: TextView
    private lateinit var adBody: TextView
    private lateinit var adMediaView: MediaView
    private lateinit var adBtn: Button

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    var btnText = "Click"

    fun setButtonData(jsonObject: JSONObject) {

        btnText = jsonObject.optString("btn_text", "")

        if (!jsonObject.optBoolean("is_default", true)) {

            val gdDefault = GradientDrawable()
            gdDefault.setColor(Color.parseColor(jsonObject.optString("btn_color", "#4285f4")))
            gdDefault.cornerRadius = jsonObject.optDouble("btn_radius", 20.0).toFloat()
            gdDefault.setStroke(
                jsonObject.optInt("btn_border_size", 0),
                Color.parseColor(jsonObject.optString("btn_border_color", "#4285f4"))
            )

            adBtn.background = gdDefault

            adBtn.setTextColor(
                Color.parseColor(
                    jsonObject.optString(
                        "btn_text_color",
                        ""
                    )
                )
            )

            val ll_back = GradientDrawable()

            ll_back.setColor(Color.parseColor(jsonObject.optString("background_color", "#FFFFFF")))
            ll_back.cornerRadius = jsonObject.optDouble("native_border_radius", 20.0).toFloat()
            ll_back.setStroke(
                10,
                Color.parseColor(jsonObject.optString("border_color", "#3F3F3F"))
            )
            adLlBackground.background = ll_back

            adTitle.setTextColor(
                Color.parseColor(
                    jsonObject.optString(
                        "native_title_text_color",
                        "#212F3D"
                    )
                )
            )

            val color = Color.parseColor(
                jsonObject.optString(
                    "native_body_text_color",
                    "#566573"
                )
            )

            adAdvertiser.setTextColor(color)
            adPrice.setTextColor(color)
            adStore.setTextColor(color)
            adBody.setTextColor(color)

        }
    }

    fun setNativeAd(nativeAd: NativeAd) {
        this.nativeAd = nativeAd
        nativeAdView.callToActionView = nativeAdView
        nativeAdView.iconView = adIconView
        nativeAdView.headlineView = adTitle
        nativeAdView.advertiserView = adAdvertiser
        nativeAdView.starRatingView = adStars
        nativeAdView.priceView = adPrice
        nativeAdView.storeView = adStore
        nativeAdView.bodyView = adBody
        nativeAdView.mediaView = adMediaView
        nativeAdView.callToActionView = adBtn

        if (nativeAd.icon != null) {
            adIconView.visibility = VISIBLE
            adIconView.setImageDrawable(nativeAd.icon!!.drawable)
        } else {
            adIconView.visibility = GONE
        }

        if (nativeAd.headline != null) {
            adTitle.visibility = VISIBLE
            adTitle.text = nativeAd.headline
        } else {
            adTitle.visibility = GONE
        }

        if (nativeAd.advertiser != null) {
            adAdvertiser.visibility = VISIBLE
            adAdvertiser.text = nativeAd.advertiser
        } else {
            adAdvertiser.visibility = GONE
        }

        if (nativeAd.starRating != null) {
            adStars.visibility = VISIBLE
            adStars.rating = nativeAd.starRating!!.toFloat()
        } else {
            adStars.visibility = View.GONE
        }

        if (nativeAd.price != null) {
            adPrice.visibility = VISIBLE
            adPrice.text = nativeAd.price
            if (nativeAd.price == "0")
                adPrice.visibility = GONE
        } else {
            adPrice.visibility = GONE
        }

        if (nativeAd.store != null) {
            adStore.visibility = VISIBLE
            adStore.text = nativeAd.store
        } else {
            adStore.visibility = GONE
        }
        if (nativeAd.body != null) {
            adBody.visibility = VISIBLE
            adBody.text = nativeAd.body
        } else {
            adBody.visibility = GONE
        }

        if (nativeAd.callToAction != null) {
            adBtn.visibility = VISIBLE

            if (btnText != "") {
                adBtn.text = btnText
            } else {
                adBtn.text = nativeAd.callToAction
            }

        } else {
            adBtn.visibility = GONE
            if (btnText != "") {
                adBtn.text = btnText
                adBtn.visibility = VISIBLE
            }

        }

        if (nativeAd.mediaContent != null) {
            adMediaView.setMediaContent(nativeAd.mediaContent!!)
        }

        nativeAdView.setNativeAd(this.nativeAd)
    }

    fun destroyNativeAd() {
        nativeAd.destroy()
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.ad_admob_native, this)
    }

    public override fun onFinishInflate() {
        super.onFinishInflate()

        nativeAdView = findViewById(R.id.native_ad_view)
        adLlBackground = findViewById(R.id.ad_ll_background)
        adIconView = findViewById(R.id.ad_img_icon)
        adTitle = findViewById(R.id.ad_title)
        adAdvertiser = findViewById(R.id.ad_advertiser)
        adStars = findViewById(R.id.ad_stars)
        adPrice = findViewById(R.id.ad_price)
        adStore = findViewById(R.id.ad_store)
        adBody = findViewById(R.id.ad_body)
        adMediaView = findViewById(R.id.ad_media_view)
        adBtn = findViewById(R.id.ad_btn)

    }


}