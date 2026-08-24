package com.example.bmi.ui.input

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R
import com.example.bmi.databinding.DialogTimePickerBinding

class TimePickerDialog(
    context: Context,
    private val onDoneClick: (Int) -> Unit
) : Dialog(context, R.style.DatePickerDialogStyle) {

    private lateinit var binding: DialogTimePickerBinding

    private lateinit var timeAdapter: PickerAdapter

    private val times = listOf(
        context.getString(R.string.morning),
        context.getString(R.string.afternoon),
        context.getString(R.string.evening),
        context.getString(R.string.night)
    )

    private var selectedTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DialogTimePickerBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        setupTimePicker()

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.done.setOnClickListener {
            onDoneClick(selectedTime)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        window?.apply {

            setGravity(Gravity.BOTTOM)

            setBackgroundDrawableResource(
                android.R.color.transparent
            )

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

    private fun setupTimePicker() {

        timeAdapter = setupRecyclerView(
            binding.time,
            times,
            getCurrentTimeSlot()
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

        lateinit var adapter: PickerAdapter

        adapter = PickerAdapter(
            data,
            itemLayoutId = R.layout.item_time_picker,
            textViewId = R.id.tvTime
        ) { position ->

            adapter.setSelectedPosition(
                position
            )

            smoothScrollToCenter(
                recyclerView,
                layoutManager,
                position
            )

            onItemSelected(position)
        }

        recyclerView.adapter = adapter

        adapter.setSelectedPosition(
            initialPosition
        )

        // 和日期选择器保持一致
        recyclerView.setPadding(
            0,
            dpToPx(102),
            0,
            dpToPx(102)
        )

        val snapHelper =
            DatePickerSnapHelper()

        snapHelper.attachToRecyclerView(
            recyclerView
        )

        recyclerView.addItemDecoration(
            object : RecyclerView.ItemDecoration() {

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {

                    val position =
                        parent.getChildAdapterPosition(
                            view
                        )

                    if (
                        position != RecyclerView.NO_POSITION &&
                        position <
                        parent.adapter!!.itemCount - 1
                    ) {

                        outRect.bottom =
                            dpToPx(15)
                    }
                }
            }
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
        recyclerView.post {

            layoutManager.scrollToPosition(
                initialPosition
            )

            recyclerView.post {

                val targetView =
                    layoutManager.findViewByPosition(
                        initialPosition
                    ) ?: return@post

                val textView =
                    targetView.findViewById<TextView>(
                        R.id.tvTime
                    )

                val recyclerViewCenter =
                    recyclerView.paddingTop +
                            (
                                    recyclerView.height -
                                            recyclerView.paddingTop -
                                            recyclerView.paddingBottom
                                    ) / 2

                val textViewCenter =
                    targetView.top +
                            textView.top +
                            textView.height / 2

                val dy =
                    textViewCenter -
                            recyclerViewCenter

                recyclerView.scrollBy(
                    0,
                    dy
                )
            }
        }

        return adapter
    }

    private fun smoothScrollToCenter(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager,
        position: Int
    ) {

        val targetView =
            layoutManager.findViewByPosition(
                position
            )

        if (targetView == null) {

            layoutManager.scrollToPosition(
                position
            )

            recyclerView.post {

                smoothScrollToCenter(
                    recyclerView,
                    layoutManager,
                    position
                )
            }

            return
        }

        val textView =
            targetView.findViewById<TextView>(
                R.id.tvTime
            )

        val recyclerViewCenter =
            recyclerView.paddingTop +
                    (
                            recyclerView.height -
                                    recyclerView.paddingTop -
                                    recyclerView.paddingBottom
                            ) / 2

        val textViewCenter =
            targetView.top +
                    textView.top +
                    textView.height / 2

        val dy =
            textViewCenter -
                    recyclerViewCenter

        if (dy != 0) {

            recyclerView.smoothScrollBy(
                0,
                dy
            )
        }
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

}