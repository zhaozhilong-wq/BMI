package com.example.bmi.ui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.example.bmi.R

object CustomPopup {

    private var popupWindow: PopupWindow? = null

    fun show(
        context: Context,
        anchor: View,
        message: String,
        iconRes: Int? = null
    ) {

        if (!anchor.isAttachedToWindow) {
            return
        }

        val view = LayoutInflater.from(context)
            .inflate(
                R.layout.popup_toast,
                null
            )

        val toastText =
            view.findViewById<TextView>(
                R.id.toastText
            )

        val toastIcon =
            view.findViewById<ImageView>(
                R.id.toastIcon
            )

        toastText.text = message

        if (iconRes != null) {
            toastIcon.visibility = View.VISIBLE
            toastIcon.setImageResource(iconRes)
        } else {
            toastIcon.visibility = View.GONE
        }

        val displayMetrics = context.resources.displayMetrics

        val screenWidth = displayMetrics.widthPixels

        val horizontalMargin =
            (30 * displayMetrics.density).toInt()

        val popupWidth =
            screenWidth - horizontalMargin * 2

        popupWindow = PopupWindow(
            view,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )

        popupWindow?.showAtLocation(
            anchor,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            0,
            150
        )

        view.postDelayed({
            dismiss()
        }, 2000L)
    }
    fun dismiss() {
        popupWindow?.dismiss()
        popupWindow = null
    }
}