package com.example.bmi.ui.result

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.example.bmi.databinding.DialogConfirmBinding

class ConfirmDialog(
    context: Context
) : Dialog(context) {

    private lateinit var binding: DialogConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDialog()

        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupDialog() {

        window?.apply {

            // 整个 Dialog 居中
            setGravity(Gravity.CENTER)

            // 去掉系统默认 Dialog 背景
            setBackgroundDrawableResource(
                android.R.color.transparent
            )

            // 左右各留 24dp
            val margin = dp(37)

            val screenWidth =
                context.resources.displayMetrics.widthPixels

            val dialogWidth =
                screenWidth - margin * 2

            setLayout(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // 点击外部是否关闭
        setCanceledOnTouchOutside(true)

        // 返回键是否关闭
        setCancelable(true)
    }

    private fun dp(value: Int): Int {
        return (
                value * context.resources.displayMetrics.density
                ).toInt()
    }
}