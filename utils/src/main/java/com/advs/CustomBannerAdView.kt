package com.advs

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.widget.*
import com.bumptech.glide.Glide
import com.R
import com.model.CustomBanner
import org.json.JSONObject

class CustomBannerAdView(
    var context: Context,
    customNativeView: LinearLayout,
    customBannerId: CustomBanner,
    nativeBtnConfig: JSONObject
) {


    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    init {


        val adIconView = customNativeView.findViewById<ImageView>(R.id.ad_img_icon)
        val adMediaView = customNativeView.findViewById<ImageView>(R.id.ad_media_view)
        val adTitle = customNativeView.findViewById<TextView>(R.id.ad_title)
        val adBody = customNativeView.findViewById<TextView>(R.id.ad_body)
        val adBtn = customNativeView.findViewById<Button>(R.id.ad_btn)
        val adLlBackground = customNativeView.findViewById<LinearLayout>(R.id.ad_ll_background)


        customNativeView.setOnClickListener {
            openUrl(customBannerId.url)
        }
        adIconView.setOnClickListener {
            openUrl(customBannerId.url)
        }
        adMediaView.setOnClickListener {
            openUrl(customBannerId.url)
        }
        adTitle.setOnClickListener {
            openUrl(customBannerId.url)
        }
        adBody.setOnClickListener {
            openUrl(customBannerId.url)
        }
        adBtn.setOnClickListener {
            openUrl(customBannerId.url)
        }
        adLlBackground.setOnClickListener {
            openUrl(customBannerId.url)
        }



        Glide.with(context).load(customBannerId.icon).into(adIconView)
        Glide.with(context).load(customBannerId.image).into(adMediaView)
        adTitle.text = customBannerId.title
        adBody.text = customBannerId.body
        adBtn.text = customBannerId.btnText


        if (!nativeBtnConfig.optBoolean("is_default", true)) {

            val gdDefault = GradientDrawable()
            gdDefault.setColor(Color.parseColor(nativeBtnConfig.optString("btn_color", "#4285f4")))
//            gdDefault.cornerRadius = nativeBtnConfig.optDouble("btn_radius", 20.0).toFloat()
//            gdDefault.setStroke(
//                nativeBtnConfig.optInt("btn_border_size", 0),
//                Color.parseColor(nativeBtnConfig.optString("btn_border_color", "#4285f4"))
//            )

            adBtn.background = gdDefault

            adBtn.setTextColor(
                Color.parseColor(
                    nativeBtnConfig.optString(
                        "btn_text_color", ""
                    )
                )
            )

            adTitle.setTextColor(
                Color.parseColor(
                    nativeBtnConfig.optString(
                        "native_title_text_color", "#212F3D"
                    )
                )
            )

            val color = Color.parseColor(
                nativeBtnConfig.optString(
                    "native_body_text_color", "#566573"
                )
            )
            adBody.setTextColor(color)
        }


    }

}