package com.dialogs


import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.R
import com.view.PushDownAnim

class RewardedAdsDialog(var activity: Activity) {
    private val dialog: Dialog = Dialog(activity, R.style.DialogCustomTheme)

    var btnOkay: AppCompatButton
    var tvMessage: AppCompatTextView
    var pbMain: ProgressBar

    var dialogButtonText = "Okay"


    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.rewarded_ads_dialog)
        tvMessage = dialog.findViewById(R.id.tvMessage)
        btnOkay = dialog.findViewById(R.id.btnOkay)
        pbMain = dialog.findViewById(R.id.pbMain)
        PushDownAnim.setPushDownAnimTo(btnOkay)
            .setScale(PushDownAnim.MODE_SCALE, 0.89f)
            .setOnClickListener { v ->
                dialog.dismiss()
            }
    }

    fun show(flag: Boolean = false) {
        if (flag) {
            tvMessage.text = "Opps!!\nVideo Not Available"
            btnOkay.text = dialogButtonText
            btnOkay.visibility = View.VISIBLE
            pbMain.visibility = View.GONE
        } else {
            btnOkay.visibility = View.GONE
            pbMain.visibility = View.VISIBLE
            tvMessage.text = "Loading..."
        }
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}