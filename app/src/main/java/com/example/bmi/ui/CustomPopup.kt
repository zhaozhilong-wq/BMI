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
        }//防止View生命周期已经结束后继续进行依赖Window的UI操作

        // 如果之前还有 Popup，先关闭
        popupWindow?.dismiss()
        popupWindow = null

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

        val popup = PopupWindow(
            view,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )
        popupWindow = popup

        // 显示前再次确认
        if (!anchor.isAttachedToWindow) {
            popup.dismiss()
            popupWindow = null
            return
        }

        popup?.showAtLocation(
            anchor,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            0,
            (100 * displayMetrics.density).toInt()
        )

        view.postDelayed({
            if (popup.isShowing) {
                popup.dismiss()
            }

            if (popupWindow === popup) {
                popupWindow = null
            }
        }, 2000L)
    }
    fun dismiss() {
        popupWindow?.dismiss()
        popupWindow = null
    }
}