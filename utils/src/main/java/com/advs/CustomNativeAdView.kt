package com.advs

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.widget.*
import com.bumptech.glide.Glide
import com.R
import com.model.CustomNative
import org.json.JSONObject

class CustomNativeAdView(
    var context: Context,
    customNativeView: LinearLayout,
    customNativeId: CustomNative,
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
            openUrl(customNativeId.url)
        }
        adIconView.setOnClickListener {
            openUrl(customNativeId.url)
        }
        adMediaView.setOnClickListener {
            openUrl(customNativeId.url)
        }
        adTitle.setOnClickListener {
            openUrl(customNativeId.url)
        }
        adBody.setOnClickListener {
            openUrl(customNativeId.url)
        }
        adBtn.setOnClickListener {
            openUrl(customNativeId.url)
        }
        adLlBackground.setOnClickListener {
            openUrl(customNativeId.url)
        }



        Glide.with(context).load(customNativeId.icon).into(adIconView)
        Glide.with(context).load(customNativeId.image).into(adMediaView)
        adTitle.text = customNativeId.title
        adBody.text = customNativeId.body
        adBtn.text = customNativeId.btnText


        if (!nativeBtnConfig.optBoolean("is_default", true)) {

            val gdDefault = GradientDrawable()
            gdDefault.setColor(Color.parseColor(nativeBtnConfig.optString("btn_color", "#4285f4")))
            gdDefault.cornerRadius = nativeBtnConfig.optDouble("btn_radius", 20.0).toFloat()
            gdDefault.setStroke(
                nativeBtnConfig.optInt("btn_border_size", 0),
                Color.parseColor(nativeBtnConfig.optString("btn_border_color", "#4285f4"))
            )

            adBtn.background = gdDefault

            adBtn.setTextColor(
                Color.parseColor(
                    nativeBtnConfig.optString(
                        "btn_text_color", ""
                    )
                )
            )

            val ll_back = GradientDrawable()

            ll_back.setColor(
                Color.parseColor(
                    nativeBtnConfig.optString(
                        "background_color",
                        "#FFFFFF"
                    )
                )
            )
            ll_back.cornerRadius = nativeBtnConfig.optDouble("native_border_radius", 20.0).toFloat()
            ll_back.setStroke(
                10, Color.parseColor(nativeBtnConfig.optString("border_color", "#3F3F3F"))
            )
            adLlBackground.background = ll_back

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