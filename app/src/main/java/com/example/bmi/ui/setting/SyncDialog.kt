package com.example.bmi.ui.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.bmi.R
import com.example.bmi.databinding.DialogSyncBinding
import com.example.bmi.ui.CustomPopup

class SyncDialog : DialogFragment() {
    private var _binding: DialogSyncBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogSyncBinding.inflate(
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

        binding.done.setOnClickListener {
            requireActivity().window.decorView.post {
                CustomPopup.show(
                    requireActivity(),
                    requireActivity().window.decorView,
                    getString(R.string.sync_success_toast),
                    R.drawable.success_icon
                )
            }
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {

            // 居中
            setGravity(Gravity.CENTER)

            // 去掉 Dialog 默认背景
            setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            // 310dp × 399dp
            setLayout(
                dpToPx(310),
                dpToPx(399)
            )
        }
    }
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}