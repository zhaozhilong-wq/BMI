package com.example.bmi.ui.setting

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.example.bmi.databinding.DialogSyncBinding
import com.example.bmi.databinding.DialogLogBinding

class SyncDialog  (
    context: Context,
    private val viewModel: SettingViewModel
) : Dialog(context) {
    private var _binding: DialogSyncBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogSyncBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.done.setOnClickListener {
            dismiss()
            Toast.makeText(context , "Sync successful!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        window?.apply {
            setGravity(Gravity.CENTER)

            setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            setLayout(
                dpToPx(310),
                dpToPx(399)
            )
        }
    }
    private fun dpToPx(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

}