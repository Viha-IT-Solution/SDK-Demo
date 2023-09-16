package com.dcdhameliya.dialogs

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.dcdhameliya.R
import com.dcdhameliya.view.PushDownAnim

class SplashDialog(var activity: Activity) {
    private val dialog: Dialog = Dialog(activity, R.style.DialogCustomTheme)

    var btnOkay: AppCompatButton
    var tvMessage: AppCompatTextView
    var tvTitle: AppCompatTextView
    var listener: OnDialogButtonClickListener? = null

    var dialogTitle = "Hey"
    var dialogMessage = "Hello! How are you"
    var dialogButtonText = "Okay"


    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.download_dialog)

        tvTitle = dialog.findViewById(R.id.tvTitle)
        tvMessage = dialog.findViewById(R.id.tvMessage)
        btnOkay = dialog.findViewById(R.id.btnOkay)
    }

    fun setBtnClick(l: OnDialogButtonClickListener) {
        this.listener = l
        PushDownAnim.setPushDownAnimTo(btnOkay)
            .setScale(PushDownAnim.MODE_SCALE, 0.89f)
            .setOnClickListener { v ->
                if (listener != null) listener!!.onClick(v) else Toast.makeText(
                    activity,
                    "$dialogButtonText Clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    fun show() {
        tvTitle.text = dialogTitle
        tvMessage.text = dialogMessage
        btnOkay.text = dialogButtonText
        dialog.show()
    }

    interface OnDialogButtonClickListener {
        fun onClick(v: View?)
    }
}