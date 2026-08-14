package com.example.bmi.ui.input

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.bmi.R
import com.example.bmi.databinding.ActivityInputBinding
import java.util.Calendar

class InputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputBinding

    private val calendar = Calendar.getInstance()

    private val currentYear = calendar.get(Calendar.YEAR)

    private val currentMonth = calendar.get(Calendar.MONTH)

    private val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

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

    val times = listOf(
        "Morning",
        "Afternoon",
        "Evening",
        "Night"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )

        )
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAgePicker()


        //绑定点击事件
        binding.lb.setOnClickListener {
            binding.lb.alpha = 1f
            binding.kg.alpha = 0.3f
            //weight换算并回显
        }
        binding.kg.setOnClickListener {
            binding.kg.alpha = 1f
            binding.lb.alpha = 0.3f
            //weight换算并回显
        }
        binding.ftin.setOnClickListener {
            binding.ftin.alpha = 1f
            binding.cm.alpha = 0.3f
            binding.heightFtInput.visibility= View.VISIBLE
            binding.heightInInput.visibility= View.VISIBLE
            binding.heightInput.visibility= View.GONE
            //height换算并回显
        }
        binding.cm.setOnClickListener {
            binding.cm.alpha = 1f
            binding.ftin.alpha = 0.3f
            binding.heightFtInput.visibility= View.GONE
            binding.heightInInput.visibility= View.GONE
            binding.heightInput.visibility= View.VISIBLE
            //height换算并回显
        }
        binding.maleContainer.setOnClickListener {
            binding.maleContainer.alpha = 1f
            binding.femaleContainer.alpha = 0.3f
            binding.maleTick.visibility = View.VISIBLE
            binding.femaleTick.visibility = View.GONE
            //保存性别选择
        }
        binding.femaleContainer.setOnClickListener {
            binding.femaleContainer.alpha = 1f
            binding.maleContainer.alpha = 0.3f
            binding.femaleTick.visibility = View.VISIBLE
            binding.maleTick.visibility = View.GONE
            //保存性别选择
        }
        binding.date.text = "${months[currentMonth]} $currentDay,$currentYear"
        binding.date.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this) { year, month, day ->
                    binding.date.text = "${months[month]} $day,$year"
                    //后续日期保存
                }

            datePickerDialog.show()
        }

        binding.timeSlot.text = getCurrentTimeSlot()
        binding.timeSlot.setOnClickListener {
            val timeSlotDialog = TimePickerDialog(this) { timeSlot ->
                binding.timeSlot.text = times[timeSlot]
            }
            timeSlotDialog.show()
        }


    }

    fun updateSelectedItem(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager,
        snapHelper: SnapHelper
    ) {
        // 所有可见 item 先变灰
        for (i in 0 until recyclerView.childCount) {
            val child =
                recyclerView.getChildAt(i)
            child.findViewById<TextView>(
                R.id.tvNumber
            ).alpha = 0.3f
        }
        // 找到正中央的 item
        val snapView =
            snapHelper.findSnapView(
                layoutManager
            ) ?: return
        // 中间 item 变黑
        snapView.findViewById<TextView>(
            R.id.tvNumber
        ).alpha = 1f
    }

    fun setupAgePicker() {
        //年龄选择器
        val ages = (2..99).toList()
        val layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL,
            false
        )
        binding.agePicker.layoutManager = layoutManager
        binding.agePicker.post {
            // 每个数字的实际宽度：47dp
            val itemWidth =
                (55 * resources.displayMetrics.density).toInt()
            // 每个 item 左右各 9dp
            val sidePadding =
                (binding.agePicker.width - itemWidth) / 2
            binding.agePicker.setPadding(
                sidePadding,
                0,
                sidePadding,
                0
            )
            val snapHelper = OneItemSnapHelper()
            val adapter = AgePickerAdapter(
                ages = ages
            ) { position ->
                // 用户点击的数字
                val targetView =
                    layoutManager.findViewByPosition(position)
                if (targetView != null) {
                    // RecyclerView 中心
                    val recyclerCenter =
                        binding.agePicker.width / 2
                    // 被点击数字的中心
                    val itemCenter =
                        targetView.left +
                                targetView.width / 2
                    // 需要移动的距离
                    val distance =
                        itemCenter - recyclerCenter
                    // 平滑移动到中间
                    binding.agePicker.smoothScrollBy(
                        distance,
                        0
                    )
                } else {
                    // 当前 item 不在屏幕上
                    binding.agePicker.scrollToPosition(
                        position
                    )
                    binding.agePicker.post {
                        snapHelper.snapToCenter(
                            binding.agePicker
                        )
                    }
                }
            }
            binding.agePicker.adapter = adapter
            snapHelper.attachToRecyclerView(
                binding.agePicker
            )
            val initialPosition = 25 - 2
            layoutManager.scrollToPosition(
                initialPosition
            )
            binding.agePicker.post {
                snapHelper.snapToCenter(
                    binding.agePicker
                )
                updateSelectedItem(
                    binding.agePicker,
                    layoutManager,
                    snapHelper
                )
            }
            //滑动监听
            binding.agePicker.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView,
                        newState: Int
                    ) {
                        super.onScrollStateChanged(
                            recyclerView,
                            newState
                        )
                        // 停止滑动
                        if (
                            newState ==
                            RecyclerView.SCROLL_STATE_IDLE
                        ) {
                            // 更新黑色/灰色
                            updateSelectedItem(
                                recyclerView,
                                layoutManager,
                                snapHelper
                            )
                            // 找到当前中心数字
                            val snapView =
                                snapHelper.findSnapView(
                                    layoutManager
                                ) ?: return
                            val position =
                                layoutManager.getPosition(
                                    snapView
                                )
                            val age = ages[position]
                            Log.d(
                                "AgePicker",
                                "当前年龄 = $age"
                            )
                        }
                    }
                }
            )
        }
    }


    private fun getCurrentTimeSlot(): String {

        val hour = Calendar.getInstance()
            .get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> "Morning"
            in 12..17 -> "Afternoon"
            in 18..20 -> "Evening"
            else -> "Night"
        }
    }



}