package com.example.bmi.ui.input

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.bmi.R
import com.example.bmi.databinding.ActivityInputBinding
import java.util.Calendar

private const val LB_TO_KG = 0.45359237
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


    private var weightKg = 0.0//后台一直只保存kg，需要lb时换算一下就行

    private var isMale = true//默认男

    private var isWeightKg = false

    private var weightChanged = false

    private var isUpdatingWeightInput = false

    private var isHeightCm = false

    private var heightChanged = false

    private var heightCm = 175.0

    private var isUpdatingHeightInput = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {//使得点击输入框外，就失去焦点
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val currentFocus = currentFocus
            if (currentFocus is EditText) {
                val location = IntArray(2)
                currentFocus.getLocationOnScreen(location)
                val left = location[0]
                val top = location[1]
                val right = left + currentFocus.width
                val bottom = top + currentFocus.height
                val x = ev.rawX
                val y = ev.rawY
                // 点击的位置不在当前 EditText 内
                if (x < left || x > right || y < top || y > bottom) {
                    currentFocus.clearFocus()
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

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


        //体重相关
        //绑定点击事件
        binding.lb.setOnClickListener {
            binding.lb.alpha = 1f
            binding.kg.alpha = 0.3f
            // 本来就是 lb
            if (!isWeightKg) {
                return@setOnClickListener
            }


            var lb = kgToLb(weightKg)

            if(lb<2){
                lb=2.0
            }else if (lb>551)
                lb=551.0

            isWeightKg = false

            if (!weightChanged) {
                setWeightText("140.00")
            } else {
                setWeightText(
                    String.format("%.2f", lb)
                )
            }
        }
        binding.kg.setOnClickListener {
            binding.kg.alpha = 1f
            binding.lb.alpha = 0.3f
            // 本来就是 kg
            if (isWeightKg) {
                return@setOnClickListener
            }

            if(weightKg<1) weightKg=1.0
            else if (weightKg>250) weightKg=250.0

            isWeightKg = true
            if (!weightChanged) {
                setWeightText("65.00")
            } else {
                setWeightText(
                    String.format("%.2f", weightKg)
                )
            }
        }

        binding.weightInput.doAfterTextChanged { text ->
            if (isUpdatingWeightInput) {
                return@doAfterTextChanged
            }
            val value =
                text?.toString()?.toDoubleOrNull()
                    ?: return@doAfterTextChanged
            weightChanged = true
            weightKg = if (isWeightKg) {
                value
            } else {
                lbToKg(value)
            }
        }

        binding.weightInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateWeight()
            }
        }


        //高度相关
        binding.cm.setOnClickListener {
            binding.cm.alpha = 1f
            binding.ftin.alpha = 0.3f
            // 本来就是 CM，不需要任何操作
            if (isHeightCm) {
                return@setOnClickListener
            }
            isHeightCm = true
            binding.heightFtInput.visibility = View.GONE
            binding.heightInInput.visibility = View.GONE
            binding.heightFtUnit.visibility = View.GONE
            binding.heightInUnit.visibility = View.GONE
            binding.heightInput.visibility = View.VISIBLE
            if (!heightChanged) {
                // 初始默认值
                setHeightCmText("170.0")
            } else {
                // 使用后台保存的完整 cm
                setHeightCmText(
                    String.format(
                        "%.1f",
                        heightCm
                    )
                )
            }
        }
        binding.ftin.setOnClickListener {

            binding.ftin.alpha = 1f
            binding.cm.alpha = 0.3f
            // 本来就是 FT/IN
            if (!isHeightCm) {
                return@setOnClickListener
            }
            isHeightCm = false
            binding.heightFtInput.visibility = View.VISIBLE
            binding.heightInInput.visibility = View.VISIBLE
            binding.heightFtUnit.visibility = View.VISIBLE
            binding.heightInUnit.visibility = View.VISIBLE
            binding.heightInput.visibility = View.GONE
            if (!heightChanged) {
                // 初始默认值
                setHeightFtInText(
                    5,
                    8
                )
            } else {
                // 后台 cm → ft + in
                val (feet, inches) =
                    cmToFtIn(heightCm)
                setHeightFtInText(
                    feet,
                    inches
                )
            }
        }
        binding.heightInput.doAfterTextChanged { text ->
            if (isUpdatingHeightInput) {
                return@doAfterTextChanged
            }
            val value =
                text?.toString()?.toDoubleOrNull()
                    ?: return@doAfterTextChanged
            heightChanged = true
            heightCm = value
        }
        binding.heightFtInput.doAfterTextChanged {

            if (isUpdatingHeightInput) {
                return@doAfterTextChanged
            }

            updateHeightFromFtIn()
        }
        binding.heightInInput.doAfterTextChanged {

            if (isUpdatingHeightInput) {
                return@doAfterTextChanged
            }

            updateHeightFromFtIn()
        }

        //ft-in单位设置



        //性别
        binding.maleContainer.setOnClickListener {
            binding.maleContainer.alpha = 1f
            binding.femaleContainer.alpha = 0.3f
            binding.maleTick.visibility = View.VISIBLE
            binding.femaleTick.visibility = View.GONE
            //保存性别选择
            isMale = true
        }
        binding.femaleContainer.setOnClickListener {
            binding.femaleContainer.alpha = 1f
            binding.maleContainer.alpha = 0.3f
            binding.femaleTick.visibility = View.VISIBLE
            binding.maleTick.visibility = View.GONE
            //保存性别选择
            isMale = false
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
            false//最后一个false意思是不反向排列
        )
        binding.agePicker.layoutManager = layoutManager
        binding.agePicker.post {//等RecyclerView完成当前这一轮布局之后，再执行里面的代码。view当前可能还没完成测量和布局
            // 每个数字的实际宽度
            val itemWidth =
                (55 * resources.displayMetrics.density).toInt()//这个只是为了计算下面的sidepadding
            // 每个 item 左右各 9dp
            val sidePadding =
                (binding.agePicker.width - itemWidth) / 2
            binding.agePicker.setPadding(//设置最左中最右两边的padding
                sidePadding,
                0,
                sidePadding,
                0
            )
            val snapHelper = OneItemSnapHelper()//滚动停止，让当前item居中
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
            )//绑定snaphelper和adapter
            val initialPosition = 25 - 2
            layoutManager.scrollToPosition(
                initialPosition
            )
            binding.agePicker.post {
                snapHelper.snapToCenter(
                    binding.agePicker
                )//设置初始位置并让他居中
                updateSelectedItem(
                    binding.agePicker,
                    layoutManager,
                    snapHelper//更新选中的颜色
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

    private fun kgToLb(kg: Double): Double {
        return kg / LB_TO_KG
    }

    private fun lbToKg(lb: Double): Double {
        return lb * LB_TO_KG
    }

    private fun setWeightText(value: String) {

        isUpdatingWeightInput = true

        binding.weightInput.setText(value)

        isUpdatingWeightInput = false
    }

    private fun ftInToCm(
        feet: Int,
        inches: Int
    ): Double {

        return feet * 30.48 +
                inches * 2.54
    }

    private fun cmToFtIn(cm: Double): Pair<Int, Int> {

        val totalInches =
            cm / 2.54

        val feet =
            (totalInches / 12).toInt()

        val inches =
            (totalInches % 12).toInt()

        return Pair(feet, inches)
    }

    private fun setHeightCmText(
        value: String
    ) {

        isUpdatingHeightInput = true

        binding.heightInput.setText(value)


        isUpdatingHeightInput = false
    }

    private fun setHeightFtInText(
        feet: Int,
        inches: Int
    ) {
        isUpdatingHeightInput = true
        binding.heightFtInput.setText(
            feet.toString()
        )
        binding.heightInInput.setText(
            inches.toString()
        )
        isUpdatingHeightInput = false
    }

    private fun updateHeightFromFtIn() {

        val feet =
            binding.heightFtInput.text
                .toString()
                .toIntOrNull()
                ?: return

        val inches =
            binding.heightInInput.text
                .toString()
                .toIntOrNull()
                ?: return

        heightChanged = true

        heightCm =
            ftInToCm(
                feet,
                inches
            )
    }


    private fun showInvalidWeightToast() {
        val range = if (isWeightKg) {
            "1-250 kg"
        } else {
            "2-551 lb"
        }
        Toast.makeText(
            this,
            "Please input a valid weight ($range) to calculate your BMI accurately.",
            Toast.LENGTH_SHORT
        ).show()
    }
    //校验体重
    private fun validateWeight() {

        val text = binding.weightInput.text.toString().trim()

        // 空
        if (text.isEmpty()) {

            val defaultValue = if (isWeightKg) {
                "65.00"
            } else {
                "140.00"
            }

            setWeightText(defaultValue)

            // 同时更新后台 kg
            weightKg = if (isWeightKg) {
                65.0
            } else {
                lbToKg(140.0)
            }

            weightChanged = true

            showInvalidWeightToast()

            return
        }

        val value = text.toDoubleOrNull()

        // 无法转换
        if (value == null) {

            val defaultValue = if (isWeightKg) {
                "65.00"
            } else {
                "140.00"
            }

            setWeightText(defaultValue)

            weightKg = if (isWeightKg) {
                65.0
            } else {
                lbToKg(140.0)
            }

            weightChanged = true

            showInvalidWeightToast()

            return
        }

        val min = if (isWeightKg) {
            1.0
        } else {
            2.0
        }

        val max = if (isWeightKg) {
            250.0
        } else {
            551.0
        }

        // 小于下限
        if (value < min) {

            val correctedValue =
                String.format("%.2f", min)

            setWeightText(correctedValue)

            updateWeightKg(min)

            showInvalidWeightToast()

            return
        }

        // 大于上限
        if (value > max) {

            val correctedValue =
                String.format("%.2f", max)

            setWeightText(correctedValue)

            updateWeightKg(max)

            showInvalidWeightToast()

            return
        }

        // 合法值
        val formattedValue =
            String.format("%.2f", value)

        setWeightText(formattedValue)

        updateWeightKg(value)
    }

    private fun updateWeightKg(value: Double) {

        weightChanged = true

        weightKg = if (isWeightKg) {
            value
        } else {
            lbToKg(value)
        }
    }



}