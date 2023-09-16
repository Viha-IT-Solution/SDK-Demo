package com.dcdhameliya.adsutils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dcdhameliya.R
import com.dcdhameliya.adsutils.GetData.Companion.MORE_APPS

class AppListAdapter(
    var context: Context,
    val listener: OnAppClickListener
) :
    RecyclerView.Adapter<AppListAdapter.MyViewHolder>() {

    interface OnAppClickListener {
        fun onAppClick(url: String)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivAppLogo: ImageView = view.findViewById<View>(R.id.ivAppLogo) as AppCompatImageView
        var llMain: LinearLayout = view.findViewById<View>(R.id.cvMain) as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_more_apps_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val jsonObj = MORE_APPS.getJSONObject(position)

        if (jsonObj.getString("logo").contains(".gif")) {
            Glide.with(context)
                .asGif()
                .load(jsonObj.getString("logo"))
                .into(holder.ivAppLogo)
        } else {
            Glide.with(context)
                .load(jsonObj.getString("logo"))
                .into(holder.ivAppLogo)
        }

        holder.llMain.setOnClickListener {
            listener.onAppClick(jsonObj.getString("url"))
        }

    }

    override fun getItemCount(): Int {
        return MORE_APPS.length()
    }

    companion object {
        var id: String? = null
        var title: String? = null
    }
}