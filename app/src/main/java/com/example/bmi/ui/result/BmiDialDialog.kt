package com.example.bmi.ui.result

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.databinding.DialogBmiDialBinding
import com.example.bmi.ui.result.category.BmiCategoryViewHelper
import kotlin.getValue

class BmiDialDialog(
    context: Context,
    private val record: BmiRecord
) : Dialog(context) {

    private var _binding: DialogBmiDialBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResultViewModel by lazy {
        ViewModelProvider(
            (context as ComponentActivity)
        )[ResultViewModel::class.java]
    }


    private fun setupCategories() {
        val container =
            binding.bmiCategoryLayout.bmiCategoryContainer
        if (record.isChild) {
            val childThreshold = viewModel.getChildThreshold(record)
            if (childThreshold == null) {
                Log.e(
                    "ResultFragment",
                    "No BMI threshold found: gender=${record.gender}, age=${record.age}"
                )
                return
            }
            val items =
                BmiCategoryViewHelper.createChildCategoryItems(
                    childThreshold
                )
            val selectedCategory =
                viewModel.getChildCategory(
                    record.bmi.toFloat(),
                    childThreshold
                )

            BmiCategoryViewHelper.setup(
                container = container,
                items = items,
                selectedCategory = selectedCategory,
                inflater = layoutInflater
            )

        } else {

            val items =
                BmiCategoryViewHelper.createAdultCategoryItems()

            val selectedCategory =
                viewModel.getAdultCategory(record.bmi.toFloat())

            BmiCategoryViewHelper.setup(
                container = container,
                items = items,
                selectedCategory = selectedCategory,
                inflater = layoutInflater
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogBmiDialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bmiDialView.setConfig(
            viewModel.getDialConfig(record)
        )

        setupCategories()
        binding.gotButton.setOnClickListener {
            dismiss()
        }
        if (record.isChild) {
            binding.title.text = "BMI for teenagers"
            val gender = if (record.gender == "Male") "Boy" else "Girl"
            binding.subTitle.text = context.getString(
                R.string.bmi_teenager_info_tip,
                record.age,
                gender
            )
            binding.subTitle.visibility = android.view.View.VISIBLE
        }else{
            binding.title.text = "BMI for adults"
            binding.subTitle.visibility = android.view.View.GONE
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
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
        }
    }

}