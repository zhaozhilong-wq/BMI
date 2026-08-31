package com.example.bmi.ui.input

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
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R
import com.example.bmi.databinding.DialogDatePickerBinding
import java.util.Calendar

class DatePickerDialog: DialogFragment() {

    private lateinit var monthAdapter: PickerAdapter
    private lateinit var dayAdapter: PickerAdapter
    private lateinit var yearAdapter: PickerAdapter


    private var _binding: DialogDatePickerBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()

    private val currentYear = calendar.get(Calendar.YEAR)

    private var selectedYear: Int = currentYear

    // 当前月份
    // Calendar.MONTH：January = 0，December = 11
    val currentMonth =
        Calendar.getInstance().get(Calendar.MONTH)

    // 当前日期
    val currentDay =
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    private var selectedMonth: Int = currentMonth

    private var selectedDay: Int = currentDay

    // 月份
    val months = listOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "June",
        "July",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedYear =
            savedInstanceState?.getInt(KEY_YEAR)
                ?: arguments?.getInt(KEY_YEAR)
                        ?: currentYear
        selectedMonth =
            savedInstanceState?.getInt(KEY_MONTH)
                ?: arguments?.getInt(KEY_MONTH)
                        ?: currentMonth
        selectedDay =
            savedInstanceState?.getInt(KEY_DAY)
                ?: arguments?.getInt(KEY_DAY)
                        ?: currentDay
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogDatePickerBinding.inflate(
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
        super.onViewCreated(
            view,
            savedInstanceState
        )
        setupDatePicker()
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.done.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle().apply {
                    putInt(KEY_YEAR, selectedYear)
                    putInt(KEY_MONTH, selectedMonth)
                    putInt(KEY_DAY, selectedDay)
                }
            )
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {

            setGravity(Gravity.BOTTOM)

            setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                dpToPx(380f)
            )
        }
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    private fun setupDatePicker() {

        val space15dp =
            dpToPx(15f)

        // 月份
        monthAdapter = setupRecyclerView(
            binding.month,
            getAvailableMonths(),
            selectedMonth,
        ) { position ->

            selectedMonth = position

            updateDay()
        }

        // 日期
        dayAdapter = setupRecyclerView(
            binding.day,
            getAvailableDays(),
            selectedDay - 1,
        ) { position ->

            selectedDay = position + 1
        }

        // 年份
        val years =
            (1900..currentYear)
                .map { it.toString() }

        yearAdapter = setupRecyclerView(
            binding.year,
            years,
            currentYear - 1900,
        ) { position ->

            selectedYear =
                1900 + position

            updateMonthAndDay()
        }
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        data: List<String>,
        initialPosition: Int,
        onItemSelected: (Int) -> Unit
    ): PickerAdapter {

        val layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        recyclerView.layoutManager = layoutManager

        recyclerView.overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        lateinit var adapter: PickerAdapter

        adapter = PickerAdapter(data,
            itemLayoutId = R.layout.item_date_picker,
            textViewId = R.id.tvDate) { position ->

            recyclerView.smoothScrollToPosition(position)

        }



        recyclerView.adapter = adapter

        adapter.setSelectedPosition(initialPosition)

        recyclerView.setPadding(
            0,
            dpToPx(101f),
            0,
            dpToPx(101f)
        )

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)


        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {

                    super.onScrollStateChanged(
                        recyclerView,
                        newState
                    )

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                        val snapView =
                            snapHelper.findSnapView(
                                layoutManager
                            ) ?: return

                        val position =
                            layoutManager.getPosition(
                                snapView
                            )

                        adapter.setSelectedPosition(position)

                        onItemSelected(position)
                    }
                }
            }
        )

        recyclerView.post {

            layoutManager.scrollToPosition(initialPosition)

            recyclerView.post {

                val snapView =
                    snapHelper.findSnapView(layoutManager)

                if (snapView != null) {

                    val distance =
                        snapHelper.calculateDistanceToFinalSnap(
                            layoutManager,
                            snapView
                        )

                    if (distance != null) {
                        recyclerView.scrollBy(
                            distance[0],
                            distance[1]
                        )
                    }
                }

                adapter.setSelectedPosition(
                    initialPosition
                )

                onItemSelected(
                    initialPosition
                )
            }
        }

        return adapter
    }




    private fun getAvailableMonths(): List<String> {

        val maxMonth =
            if (selectedYear == currentYear) {
                calendar.get(Calendar.MONTH)
            } else {
                11
            }

        return months.take(maxMonth + 1)
    }

    private fun getAvailableDays(): List<String> {

        val maxDay: Int

        if (selectedYear == currentYear &&
            selectedMonth == calendar.get(Calendar.MONTH)
        ) {

            maxDay =
                calendar.get(Calendar.DAY_OF_MONTH)

        } else {

            val tempCalendar =
                Calendar.getInstance()

            tempCalendar.set(
                selectedYear,
                selectedMonth + 1,
                0
            )

            maxDay =
                tempCalendar.get(
                    Calendar.DAY_OF_MONTH
                )
        }

        return (1..maxDay).map {
            it.toString()
        }
    }

    private fun updateMonthAndDay() {

        val availableMonths =
            getAvailableMonths()

        // 记录原来的月份
        val oldSelectedMonth = selectedMonth
        if (selectedMonth > availableMonths.lastIndex) {
            selectedMonth =
                availableMonths.lastIndex
        }

        // 更新月份列表
        monthAdapter.updateData(
            availableMonths
        )

        if (selectedMonth != oldSelectedMonth) {


            binding.month.post {
                binding.month.smoothScrollToPosition(selectedMonth)
            }
        }

        // 更新日期
        updateDay()
    }

    private fun updateDay() {

        val availableDays =
            getAvailableDays()

        // 记录原来的日期
        val oldSelectedDay = selectedDay

        // 只有原来的日期不存在了，才修正日期
        if (selectedDay > availableDays.size) {
            selectedDay = availableDays.size
        }

        // 更新日期列表
        dayAdapter.updateData(
            availableDays
        )

        dayAdapter.setSelectedPosition(
            selectedDay - 1
        )


        if (selectedDay == oldSelectedDay) {
            return
        }

        binding.day.post {

            binding.day.smoothScrollToPosition(
                selectedDay - 1
            )

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val REQUEST_KEY =
            "date_picker_result"

        const val KEY_YEAR =
            "year"

        const val KEY_MONTH =
            "month"

        const val KEY_DAY =
            "day"

        fun newInstance(
            year: Int,
            month: Int,
            day: Int
        ): DatePickerDialog {

            return DatePickerDialog().apply {
                arguments = Bundle().apply {
                    putInt(
                        KEY_YEAR,
                        year
                    )
                    putInt(
                        KEY_MONTH,
                        month
                    )
                    putInt(
                        KEY_DAY,
                        day
                    )
                }
            }
        }
    }


}


