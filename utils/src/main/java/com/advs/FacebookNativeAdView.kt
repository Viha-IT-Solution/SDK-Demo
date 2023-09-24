package com.advs

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.R
import org.json.JSONObject

class FacebookNativeAdView(
    context: android.content.Context,
    nativeAd: com.facebook.ads.NativeAd,
    jsonObject: JSONObject,
    nativeAdView: com.facebook.ads.NativeAdLayout
) {

    init {
        val btnText = jsonObject.optString("btn_text", "")

        nativeAd.unregisterView()
        val adChoicesContainer: LinearLayout = nativeAdView.findViewById(R.id.ad_choices_container)
        val adOptionsView = com.facebook.ads.AdOptionsView(context, nativeAd, nativeAdView)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        val adIconView =
            nativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.ad_img_icon)
        val adTitle = nativeAdView.findViewById<TextView>(R.id.ad_title)
        val adMediaView =
            nativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.ad_media_view)
        val adSocialContext =
            nativeAdView.findViewById<TextView>(R.id.native_ad_social_context)
        val adBody = nativeAdView.findViewById<TextView>(R.id.ad_body)
        val adSponsoredLabel = nativeAdView.findViewById<TextView>(R.id.native_ad_sponsored_label)
        val adBtn = nativeAdView.findViewById<Button>(R.id.ad_btn)

        adTitle.text = nativeAd.advertiserName
        adBody.text = nativeAd.adBodyText
        adSocialContext.text = nativeAd.adSocialContext
        adBtn.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE

        if (btnText != "") {
            adBtn.text = btnText
        } else {
            adBtn.text = nativeAd.adCallToAction
        }


        adSponsoredLabel.text = nativeAd.sponsoredTranslation
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(adTitle)
        clickableViews.add(adBtn)

        nativeAd.registerViewForInteraction(
            nativeAdView, adMediaView, adIconView, clickableViews
        )

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
                        "btn_text_color", ""
                    )
                )
            )

            val ad_ll_background = nativeAdView.findViewById<LinearLayout>(R.id.ad_ll_background)
            val ll_back = GradientDrawable()

            ll_back.setColor(Color.parseColor(jsonObject.optString("background_color", "#FFFFFF")))
            ll_back.cornerRadius = jsonObject.optDouble("native_border_radius", 20.0).toFloat()
            ll_back.setStroke(
                10,
                Color.parseColor(jsonObject.optString("border_color", "#3F3F3F"))
            )
            ad_ll_background.background = ll_back

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

            adBody.setTextColor(color)
            adSocialContext.setTextColor(color)
            adSponsoredLabel.setTextColor(color)


        }
    }
}