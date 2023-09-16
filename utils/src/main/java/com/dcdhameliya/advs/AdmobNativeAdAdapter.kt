package com.dcdhameliya.advs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.dcdhameliya.adsutils.GetData.Companion.adsManager
import androidx.recyclerview.widget.RecyclerView
import com.dcdhameliya.R
import com.dcdhameliya.adsutils.GetData

class AdmobNativeAdAdapter private constructor(private val mParam: Param) :
    RecyclerViewAdapterWrapper(
        mParam.adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
    private fun convertAdPosition2OrgPosition(position: Int): Int {
        return position - (position + 1) / (mParam.adItemInterval + 1)
    }

    override fun getItemCount(): Int {
        val realCount = super.getItemCount()
        return realCount + realCount / mParam.adItemInterval
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAdPosition(position)) {
            TYPE_FB_NATIVE_ADS
        } else super.getItemViewType(convertAdPosition2OrgPosition(position))
    }

    private fun isAdPosition(position: Int): Boolean {
        return (position + 1) % (mParam.adItemInterval + 1) == 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            val adHolder = holder as AdViewHolder

            if (mParam.isSmallAds) {
                adsManager!!.showNativeSmallAds(adHolder.nativeAdView)
            } else {
                adsManager!!.showNativeAds(adHolder.nativeAdView)
            }

        } else {
            super.onBindViewHolder(holder, convertAdPosition2OrgPosition(position))
        }
    }

    private fun onCreateAdViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val adLayoutContent =
            LayoutInflater.from(parent.context).inflate(R.layout.list_admob_native, parent, false)
        return AdViewHolder(adLayoutContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FB_NATIVE_ADS) {
            onCreateAdViewHolder(parent)
        } else super.onCreateViewHolder(parent, viewType)
    }

    private class Param {
        var context: Context? = null
        var getData: GetData? = null
        var admobNativeId: String? = null
        var adapter: RecyclerView.Adapter<*>? = null
        var adItemInterval = 0
        var forceReloadAdOnBind = false
        var isSmallAds = false
    }

    class Builder private constructor(private val mParam: Param) {
        fun build(): AdmobNativeAdAdapter {
            return AdmobNativeAdAdapter(mParam)
        }

        companion object {
            fun with(
                context: Context?,
                getData: GetData?,
                isSmallAds: Boolean,
                wrapped: RecyclerView.Adapter<*>?,
            ): Builder {
                val param = Param()
                param.context = context
                param.getData = getData
                param.adapter = wrapped
                param.adItemInterval = getData!!.nativeAdPosition() - 1
                param.forceReloadAdOnBind = true
                param.isSmallAds = isSmallAds
                param.isSmallAds = isSmallAds
                return Builder(param)
            }
        }
    }

    private class AdViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var nativeAdView: LinearLayout
        var loaded: Boolean

        init {
            nativeAdView = view.findViewById(R.id.nativeAdView)
            loaded = false
        }
    }

    companion object {
        private const val TYPE_FB_NATIVE_ADS = 900
    }
}