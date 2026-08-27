package com.example.bmi.ui.input

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.bmi.R
import com.example.bmi.databinding.FragmentInputBinding
import com.example.bmi.ui.CustomPopup
import com.example.bmi.ui.result.ResultActivity
import com.example.bmi.ui.setting.SettingActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [InputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputFragment : Fragment() {

    private val months = listOf(
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
    private val times = listOf(
        R.string.morning,
        R.string.afternoon,
        R.string.evening,
        R.string.night
    )
    private val ages = (2..99).toList()

    private var _binding: FragmentInputBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel : InputViewModel by activityViewModel()

    private var isUpdatingWeightInput = false//区分用户修改和系统修改

    private var isUpdatingHeightInput = false//区分用户修改和系统修改


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("InputFragment", "InputFragment onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.toastEvent.collect { (resId,range) ->

                    CustomPopup.show(
                        requireContext(),
                        binding.root,
                        getString(resId, range),
                        R.drawable.warning_icon
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    updateWeightUi(uiState)
                    updateHeightUi(uiState)
                    updateDateTimeUi(uiState)
                    updateGenderUi(uiState)
                }
            }
        }
        binding.weightInput.doAfterTextChanged { text ->
            if (isUpdatingWeightInput) {
                return@doAfterTextChanged
            }
            viewModel.onWeightChanged(
                text.toString()
            )
        }
        binding.weightInput.setOnFocusChangeListener { _, hasFocus ->
            Log.d("InputFragment", "weightInput focus = $hasFocus")
            viewModel.onWeightFocusChanged(hasFocus)
        }

        binding.heightInput.doAfterTextChanged { text ->
            if (isUpdatingHeightInput) {
                return@doAfterTextChanged
            }
            viewModel.onHeightCmChanged(
                text.toString()
            )
        }

        binding.heightFtInput.doAfterTextChanged { text ->
            if (isUpdatingHeightInput) {
                return@doAfterTextChanged
            }
            viewModel.onHeightFtChanged(
                text.toString()
            )
        }

        binding.heightInInput.doAfterTextChanged { text ->
            if (isUpdatingHeightInput) {
                return@doAfterTextChanged
            }
            viewModel.onHeightInChanged(
                text.toString()
            )
        }

        binding.heightInput.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onHeightCmFocusChanged(hasFocus)
        }

        binding.heightFtInput.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onHeightFtFocusChanged(hasFocus)
        }

        binding.heightInInput.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onHeightInFocusChanged(hasFocus)
        }

        binding.lb.setOnClickListener {
            viewModel.selectWeightUnit(false)
        }

        binding.kg.setOnClickListener {
            viewModel.selectWeightUnit(true)
        }

        binding.cm.setOnClickListener {
            viewModel.selectHeightUnit(true)
        }

        binding.ftin.setOnClickListener {
            viewModel.selectHeightUnit(false)
        }


        binding.date.setOnClickListener {

            DatePickerDialog
                .newInstance(
                    year = viewModel.uiState.value.year,
                    month = viewModel.uiState.value.month,
                    day = viewModel.uiState.value.day
                )
                .show(
                    parentFragmentManager,
                    "DatePickerDialog"
                )
        }

        parentFragmentManager.setFragmentResultListener(
            DatePickerDialog.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->

            val year =
                result.getInt(DatePickerDialog.KEY_YEAR)

            val month =
                result.getInt(DatePickerDialog.KEY_MONTH)

            val day =
                result.getInt(DatePickerDialog.KEY_DAY)

            viewModel.selectDate(year, month, day)
        }
        binding.timeSlot.setOnClickListener {

            TimePickerDialog
                .newInstance(
                    selectedTime = viewModel.uiState.value.timeSlot
                )
                .show(
                    parentFragmentManager,
                    "TimePickerDialog"
                )
        }
        parentFragmentManager.setFragmentResultListener(
            TimePickerDialog.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->

            val timeSlot =
                result.getInt(
                    TimePickerDialog.KEY_TIME_SLOT
                )

            viewModel.selectTimeSlot(timeSlot)
        }

        setupAgePicker()

        binding.maleContainer.setOnClickListener {
            viewModel.selectGender(true)
        }

        binding.femaleContainer.setOnClickListener {
            viewModel.selectGender(false)
        }

        binding.calButton.setOnClickListener {
            viewModel.calculateAndSave { mode, recordId ->
                resultLauncher.launch(
                    ResultActivity.newIntent(requireContext(), mode, recordId)
                )
            }
        }

        binding.user.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }

    }

    private fun updateWeightUi(uiState: InputUiState) {
        if (uiState.isWeightKg) {
            binding.kg.alpha = 1f
            binding.lb.alpha = 0.2f
        } else {
            binding.kg.alpha = 0.2f
            binding.lb.alpha = 1f
        }
        if (binding.weightInput.text.toString() != uiState.weightText) {
            isUpdatingWeightInput = true
            binding.weightInput.setText(uiState.weightText)
            binding.weightInput.setSelection(
                binding.weightInput.text.length
            )
            isUpdatingWeightInput = false
        }
    }
    private fun updateHeightUi(uiState: InputUiState) {
        isUpdatingHeightInput = true
        if (uiState.isHeightCm) {
            binding.cm.alpha = 1f
            binding.ftin.alpha = 0.2f
            binding.heightInput.visibility = View.VISIBLE
            binding.heightFtInput.visibility = View.GONE
            binding.heightInInput.visibility = View.GONE
            binding.heightFtUnit.visibility = View.GONE
            binding.heightInUnit.visibility = View.GONE
            if (binding.heightInput.text.toString() != uiState.heightCmText) {
                binding.heightInput.setText(uiState.heightCmText)
                binding.heightInput.setSelection(
                    binding.heightInput.text.length
                )
            }
        } else {
            binding.cm.alpha = 0.2f
            binding.ftin.alpha = 1f
            binding.heightInput.visibility = View.GONE
            binding.heightFtInput.visibility = View.VISIBLE
            binding.heightInInput.visibility = View.VISIBLE
            binding.heightFtUnit.visibility = View.VISIBLE
            binding.heightInUnit.visibility = View.VISIBLE
            if (binding.heightFtInput.text.toString() != uiState.heightFtText) {
                binding.heightFtInput.setText(uiState.heightFtText)
                binding.heightFtInput.setSelection(
                    binding.heightFtInput.text.length
                )
            }
            if (binding.heightInInput.text.toString() != uiState.heightInText) {
                binding.heightInInput.setText(uiState.heightInText)
                binding.heightInInput.setSelection(
                    binding.heightInInput.text.length
                )
            }
        }
        isUpdatingHeightInput = false
    }

    private fun updateGenderUi(uiState: InputUiState) {
        if (uiState.isMale) {
            binding.maleContainer.alpha = 1f
            binding.femaleContainer.alpha = 0.3f
            binding.maleTick.visibility = View.VISIBLE
            binding.femaleTick.visibility = View.GONE
        } else {
            binding.maleContainer.alpha = 0.3f
            binding.femaleContainer.alpha = 1f
            binding.maleTick.visibility = View.GONE
            binding.femaleTick.visibility = View.VISIBLE
        }
    }

    private fun updateDateTimeUi(uiState: InputUiState) {
        binding.date.text = "${months[uiState.month]} ${uiState.day},${uiState.year}"
        binding.timeSlot.text = getString(times[uiState.timeSlot])
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
        val layoutManager = LinearLayoutManager(
            requireContext(),
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
            val snapHelper = LinearSnapHelper()//滚动停止，让当前item居中
            val adapter = AgePickerAdapter(
                ages = ages
            ){ position ->
                binding.agePicker.smoothScrollToPosition(position)

            }
            binding.agePicker.adapter = adapter
            snapHelper.attachToRecyclerView(
                binding.agePicker
            )//绑定snaphelper和adapter
            val initialPosition = 25 - 2
            binding.agePicker.smoothScrollToPosition(initialPosition)
            //吸附后，更新选中
            binding.agePicker.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView,
                        newState: Int
                    ) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            // 滚动 + SnapHelper 吸附全部结束
                            updateSelectedItem(
                                recyclerView,
                                layoutManager,
                                snapHelper
                            )
                        }
                    }
                }
            )
        }

    }

    private val resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val deleteSuccess =
                    result.data?.getBooleanExtra(
                        "delete_success",
                        false
                    ) ?: false

                if (deleteSuccess) {
                    CustomPopup.show(
                        requireContext(),
                        binding.root,
                        getString(R.string.delete_successfully),
                        R.drawable.success_icon
                    )
                }
            }
        }




}