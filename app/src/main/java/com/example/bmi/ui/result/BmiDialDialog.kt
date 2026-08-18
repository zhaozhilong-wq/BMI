package com.example.bmi.ui.result

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.example.bmi.R
import com.example.bmi.databinding.DialogBmiDialBinding
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.BmiSection

class BmiDialDialog(
    context: Context
) : Dialog(context) {

    private var _binding: DialogBmiDialBinding? = null
    private val binding get() = _binding!!

    private val adultMaleConfig = BmiDialConfig(
        minBmi = 15.6f,
        maxBmi = 40.3f,
        sections = listOf(
            BmiSection(15.6f, 16f, R.color.vsu),
            BmiSection(16f, 17f, R.color.su),
            BmiSection(17f, 18.5f, R.color.underweight),
            BmiSection(18.5f, 25f, R.color.normal),
            BmiSection(25f, 30f, R.color.overweight),
            BmiSection(30f, 35f, R.color.obesity1),
            BmiSection(35f, 40f, R.color.obesity2),
            BmiSection(40f, 40.3f, R.color.obesity3)
        ),
        ticks = listOf(
            16f,
            17f,
            18.5f,
            25f,
            30f,
            35f,
            40f
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogBmiDialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bmiDialView.setConfig(adultMaleConfig)
        binding.gotButton.setOnClickListener {
            dismiss()
        }
    }

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
                dpToPx(590)
            )
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}