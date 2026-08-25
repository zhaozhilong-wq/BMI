package com.example.bmi.ui.setting

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.bmi.databinding.DialogLogBinding
import com.example.bmi.ui.result.ResultViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class LogDialog: DialogFragment() {
    private var _binding: DialogLogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by activityViewModel()



    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {

            setGravity(Gravity.BOTTOM)

            setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogLogBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
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
            if (isLogin) {
                View.GONE
            } else {
                View.VISIBLE
            }
        binding.logout.visibility =
            if (isLogin) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}