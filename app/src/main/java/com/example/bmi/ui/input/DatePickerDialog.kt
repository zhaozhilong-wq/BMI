package com.example.bmi.ui.input

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R
import com.example.bmi.databinding.DialogDatePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar
import kotlin.text.toInt

class DatePickerDialog(
    context: Context
) : Dialog(context, R.style.DatePickerDialogStyle) {

    private lateinit var binding: DialogDatePickerBinding

    private val calendar = Calendar.getInstance()

    private val currentYear = calendar.get(Calendar.YEAR)

    private val currentMonth = calendar.get(Calendar.MONTH)

    private val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogDatePickerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupDatePicker()

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.done.setOnClickListener {
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
                dpToPx(380)
            )
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
    private fun setupDatePicker() {

        val space15dp =
            (15 * context.resources.displayMetrics.density).toInt()

        setupRecyclerView(
            binding.month,
            listOf(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
            ),
            space15dp
        )

        setupRecyclerView(
            binding.day,
            (1..31).map { it.toString() },
            space15dp
        )

        setupRecyclerView(
            binding.year,
            (1900..2026).map { it.toString() },
            space15dp
        )
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        data: List<String>,
        space15dp: Int
    ) {

        recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )

        recyclerView.adapter =
            DatePickerAdapter(data) {
                // 后面处理点击
            }

        recyclerView.addItemDecoration(
            object : RecyclerView.ItemDecoration() {

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = space15dp
                }
            }
        )
    }
}


