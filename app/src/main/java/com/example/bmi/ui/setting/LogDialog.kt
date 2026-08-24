package com.example.bmi.ui.setting

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bmi.databinding.DialogLogBinding
import com.example.bmi.ui.result.ResultViewModel

class LogDialog (
    context: Context,
    private val viewModel: SettingViewModel
) : Dialog(context) {
    private var _binding: DialogLogBinding? = null
    private val binding get() = _binding!!



    override fun onStart() {
        super.onStart()

        window?.apply {
            // 放到底部
            setGravity(Gravity.BOTTOM)
            // Dialog 本身透明
            setBackgroundDrawableResource(
                android.R.color.transparent
            )
            // Window 宽度占满屏幕
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateLoginState()

        binding.close.setOnClickListener {
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.login.setOnClickListener {
            viewModel.login()
            dismiss()
        }
        binding.logout.setOnClickListener {
            viewModel.logout()
            dismiss()
        }



    }

    private fun updateLoginState() {
        val isLogin = viewModel.isLogin.value

        binding.login.visibility =
            if (isLogin) View.GONE else View.VISIBLE

        binding.logout.visibility =
            if (isLogin) View.VISIBLE else View.GONE
    }


}