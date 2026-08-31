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
import androidx.fragment.app.DialogFragment
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R
import com.example.bmi.databinding.DialogTimePickerBinding

class TimePickerDialog: DialogFragment() {

    private var _binding: DialogTimePickerBinding? = null
    private val binding get() = _binding!!

    private lateinit var timeAdapter: PickerAdapter

    private val times by lazy {
        listOf(
        getString(R.string.morning),
        getString(R.string.afternoon),
        getString(R.string.evening),
        getString(R.string.night)
    )
    }

    private var selectedTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedTime = arguments?.getInt(
            ARG_SELECTED_TIME,
            getCurrentTimeSlot()
        ) ?: getCurrentTimeSlot()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogTimePickerBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        setupTimePicker()

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.done.setOnClickListener {

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle().apply {
                    putInt(
                        KEY_TIME_SLOT,
                        selectedTime
                    )
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

    private fun setupTimePicker() {

        timeAdapter = setupRecyclerView(
            binding.time,
            times,
            selectedTime
        ) { position ->

            selectedTime = position
        }
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        data: List<String>,
        initialPosition: Int,
        onItemSelected: (Int) -> Unit
    ): PickerAdapter {

        val layoutManager =
            LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )

        recyclerView.layoutManager =
            layoutManager

        var adapter = PickerAdapter(
            data,
            itemLayoutId = R.layout.item_time_picker,
            textViewId = R.id.tvTime
        ) { position ->

            // 点击后平滑滚动
            recyclerView.smoothScrollToPosition(
                position
            )
        }

        recyclerView.adapter = adapter

        adapter.setSelectedPosition(
            initialPosition
        )

        // 和日期选择器保持一致
        recyclerView.setPadding(
            0,
            dpToPx(101f),
            0,
            dpToPx(101f)
        )

        val snapHelper =
            LinearSnapHelper()

        snapHelper.attachToRecyclerView(
            recyclerView
        )


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

                    if (
                        newState ==
                        RecyclerView.SCROLL_STATE_IDLE
                    ) {

                        val snapView =
                            snapHelper.findSnapView(
                                layoutManager
                            ) ?: return

                        val position =
                            layoutManager.getPosition(
                                snapView
                            )

                        adapter.setSelectedPosition(
                            position
                        )

                        onItemSelected(position)
                    }
                }
            }
        )

        // 初始化到 Morning

        adapter.setSelectedPosition(initialPosition)

        recyclerView.post {

            layoutManager.scrollToPosition(initialPosition)

            adapter.setSelectedPosition(initialPosition)
            onItemSelected(initialPosition)
        }

        return adapter
    }

    private fun getCurrentTimeSlot(): Int {

        val hour = java.util.Calendar.getInstance()
            .get(java.util.Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 6..11 -> 0       // Morning
            in 12..17 -> 1      // Afternoon
            in 18..21 -> 2      // Evening
            else -> 3           // Night
        }
    }

    companion object {

        const val REQUEST_KEY =
            "TimePickerDialogResult"

        const val KEY_TIME_SLOT =
            "time_slot"

        private const val ARG_SELECTED_TIME =
            "selected_time"

        fun newInstance(
            selectedTime: Int
        ): TimePickerDialog {

            return TimePickerDialog().apply {

                arguments = Bundle().apply {
                    putInt(
                        ARG_SELECTED_TIME,
                        selectedTime
                    )
                }
            }
        }
    }

}