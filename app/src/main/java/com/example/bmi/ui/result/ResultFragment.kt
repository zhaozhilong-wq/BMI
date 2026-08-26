package com.example.bmi.ui.result

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.databinding.FragmentResultBinding
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import com.example.bmi.ui.recent.RecentActivity
import com.example.bmi.ui.result.category.BmiCategory
import com.example.bmi.ui.result.category.BmiCategoryViewHelper
import com.example.bmi.ui.result.category.BmiCategoryViewHelper.createAdultCategoryItems
import com.example.bmi.ui.result.category.BmiCategoryViewHelper.createChildCategoryItems
import com.example.bmi.ui.result.category.ChildBmiThreshold
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.math.roundToInt
import android.animation.ValueAnimator
import android.app.Activity
import android.view.ViewPropertyAnimator
import com.example.bmi.ui.result.category.BmiStatus
import com.example.bmi.ui.result.category.BmiStatusResult


/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResultViewModel by activityViewModel()

    private lateinit var mode: ResultMode
    private var recordId: Long = -1L

    private var currentRecord: BmiRecord? = null

    private var bmiValueAnimator: ValueAnimator? = null

    private var pointerAnimator: ViewPropertyAnimator? = null

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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ResultFragment", "ResultFragment onCreate")
        val args = arguments

        if (args != null) {
            mode = ResultMode.valueOf(
                args.getString(ARG_MODE)!!
            )
            recordId = args.getLong(ARG_RECORD_ID)
        } else {
            mode = ResultMode.LATEST
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        when (mode) {
            ResultMode.NORMAL -> {
                Log.d("ResultFragment", "mode is NORMAL")
                viewModel.loadRecord(recordId)
                binding.bmiCategoryLayout.bmiCategoryContainer.visibility = View.GONE
            }

            ResultMode.NEW_USER -> {
                Log.d("ResultFragment", "mode is NEW_USER")
                binding.bmiHelp.visibility = View.GONE
                binding.recommendation.visibility = View.GONE
                viewModel.loadRecord(recordId)
            }

            ResultMode.HISTORY -> {
                Log.d("ResultFragment", "mode is HISTORY")
                viewModel.loadRecord(recordId)
                binding.bmiCategoryLayout.bmiCategoryContainer.visibility = View.GONE
                binding.saveButton.visibility = View.GONE
                binding.discord.visibility = View.GONE
                binding.back.visibility = View.VISIBLE
                binding.delete.visibility = View.VISIBLE
                binding.lineText.visibility = View.VISIBLE


                binding.back.setOnClickListener {
                    requireActivity().finish()
                }
                binding.delete.setOnClickListener {
                    showConfirmDialog()
                }



            }

            ResultMode.LATEST -> {
                Log.d("ResultFragment", "mode is LATEST")
                viewModel.getLatestRecord()
                binding.advice.visibility = View.GONE
                binding.bmiHelp.visibility = View.GONE
                binding.recommendation.visibility = View.GONE
                binding.saveButton.visibility = View.GONE
                binding.discord.visibility = View.GONE
                binding.BMI.visibility = View.VISIBLE
                binding.time.visibility = View.VISIBLE
                binding.recent.visibility = View.VISIBLE

                binding.recent.setOnClickListener {
                    startActivity(Intent(requireContext(), RecentActivity::class.java))
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.record.collect { record ->
                    currentRecord = record
                    Log.d("ResultFragment", "recordid: ${record?.id}")
                    if (record == null) {
                        return@collect
                    }

                    val config = viewModel.getDialConfig(record)//获取表盘配置，分为儿童和成人
                    binding.bmiDialView.setConfig(config)//设置表盘配置
                    if (record.isChild) {

                        val childThreshold = viewModel.getChildThreshold(record)//根据年龄获取对应的各个节点的阈值
                        if (childThreshold == null) {
                            Log.e(
                                "ResultFragment",
                                "No BMI threshold found: gender=${record.gender}, age=${record.age}"
                            )
                            return@collect
                        }
                        val category = viewModel.getCategory(record)//得到dmi对应的分类
                        setupChildCategories(childThreshold,
                            selectedCategory = category)//设置所有分类并选中对应的分类
                        binding.bmiStatus.text = getString(category.displayName)
                        if (category.displayName == R.string.bmi_obese_class_i){
                            binding.bmiStatus.text = "Obese"
                        }
                        setStatusBackground(category.colorRes)
                        setAdviceText(record)

                    } else {
                        // 成年人显示分类
                        val category = viewModel.getCategory(record)//拿到计算得到的dmi对应的分类
                        setupAdultCategories(category)//设置所有分类，并且选中对应的分类
                        binding.bmiStatus.text = getString(category.displayName)
                        setStatusBackground(category.colorRes)
                        setAdviceText(record)
                    }

                    if(binding.lineText.visibility == View.VISIBLE){
                        binding.lineText.text =
                            "${months[record.month]} ${record.day}, ${record.year} " +
                                    binding.root.context.getString(times[record.time])
                    }

                    if (binding.time.visibility == View.VISIBLE) {
                        binding.time.text =
                            "${months[record.month]} ${record.day}, ${record.year} "
                    }

                    setPersonInfo(record)

                    binding.BmiPointer.post {
                        val pivotX = 38.543f / 53f * binding.BmiPointer.width
                        val pivotY = 77.617f / 92f * binding.BmiPointer.height

                        binding.BmiPointer.pivotX = pivotX
                        binding.BmiPointer.pivotY = pivotY


                        if (mode == ResultMode.NORMAL ||
                            mode == ResultMode.NEW_USER
                        ) {
                            showResultWithAnimation(
                                record,
                                config
                            )
                        } else {
                            binding.bmiResult.text =
                                String.format(
                                    "%.1f",
                                    record.bmi
                                )
                            binding.BmiPointer.rotation =
                                bmiToPointerRotation(
                                    record.bmi.toFloat(),
                                    config
                                )
                        }
                    }

                }
            }
        }

        binding.bmiHelp.setOnClickListener {
            currentRecord?.let { record ->
                BmiDialDialog
                    .newInstance(record.id)
                    .show(
                        parentFragmentManager,
                        "BmiDialDialog"
                    )
            }
        }
        binding.discord.setOnClickListener {
            showConfirmDialog()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    if (mode == ResultMode.NORMAL ||
                        mode == ResultMode.NEW_USER
                    ) {
                        showConfirmDialog()
                    }else if (mode == ResultMode.HISTORY)
                    {requireActivity().finish()}
                    else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )

        binding.saveButton.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    MainActivity::class.java
                ).apply {
                    putExtra("open_page", 2)
                    putExtra("show_saved_toast", true)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            )

            requireActivity().finish()
        }

    }

    override fun onDestroyView() {
        bmiValueAnimator?.cancel()
        bmiValueAnimator = null
        pointerAnimator?.cancel()
        pointerAnimator = null
        super.onDestroyView()
        _binding = null
    }

    private fun showConfirmDialog() {
        childFragmentManager.setFragmentResultListener(
            ConfirmDialog.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val confirmed =
                bundle.getBoolean(
                    ConfirmDialog.RESULT_KEY
                )

            if (confirmed) {
                viewModel.deleteRecord(recordId) { isNewUser ->
                    if (isNewUser && mode == ResultMode.HISTORY) {
                        startActivity(
                            Intent(
                                requireContext(),
                                InputActivity::class.java
                            ).apply {
                                putExtra(
                                    "show_delete_toast",
                                    true
                                )
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                        requireActivity().finish()
                    } else {
                        requireActivity().setResult(
                            Activity.RESULT_OK,
                            Intent().apply {
                                putExtra(
                                    "delete_success",
                                    true
                                )
                            }
                        )
                        requireActivity().finish()
                    }
                }
            }
        }

        if (
            childFragmentManager.findFragmentByTag(
                ConfirmDialog.TAG
            ) == null
        ) {
            ConfirmDialog().show(
                childFragmentManager,
                ConfirmDialog.TAG
            )
        }
    }

    companion object {

        private const val ARG_MODE = "arg_mode"
        private const val ARG_RECORD_ID = "arg_record_id"

        fun newInstance(
            mode: ResultMode,
            recordId: Long
        ): ResultFragment {

            return ResultFragment().apply {

                arguments = Bundle().apply {

                    putString(ARG_MODE, mode.name)
                    putLong(ARG_RECORD_ID, recordId)
                }
            }
        }
    }


    private fun setupChildCategories(
        threshold: ChildBmiThreshold,
        selectedCategory: BmiCategory
    ) {
        val items = createChildCategoryItems(threshold)

        BmiCategoryViewHelper.setup(
            container = binding.bmiCategoryLayout.bmiCategoryContainer,
            items = items,
            selectedCategory = selectedCategory,
            inflater = layoutInflater
        )
    }


    private fun setupAdultCategories(selectedCategory: BmiCategory) {
        val items = createAdultCategoryItems()

        BmiCategoryViewHelper.setup(
            container = binding.bmiCategoryLayout.bmiCategoryContainer,
            items = items,
            selectedCategory = selectedCategory,
            inflater = layoutInflater
        )
    }
    private fun setStatusBackground(@ColorRes colorRes: Int) {

        val drawable = binding.bmiStatusContainer.background.mutate()

        drawable.setTint(
            ContextCompat.getColor(
                requireContext(),
                colorRes
            )
        )

        binding.bmiStatusContainer.background = drawable
    }

    private fun setPersonInfo(record: BmiRecord) {

        val totalInches = (record.heightCm / 2.54).roundToInt()
        val feet = totalInches / 12
        val inches = totalInches % 12

        val weight: String
        val height: String

        // 体重
        if (record.weightUnit == "kg") {
            weight = String.format("%.2f kg", record.weightKg)
        } else {
            val lb = record.weightKg / 0.45359237
            weight = String.format("%.2f lb", lb)
        }

        // 身高
        height = if (record.heightUnit == "cm") {
            getString(
                R.string.bmi_height_cm_format,
                String.format("%.1f", record.heightCm)
            )
        } else {
            getString(
                R.string.bmi_height_ft_in_format,
                "$feet",
                "$inches"
            )
        }

        binding.personInfo.text = getString(
            R.string.bmi_input_data,
            weight,
            height,
            record.gender,
            record.age.toString()
        )
    }

    private fun bmiToPointerRotation(
        bmi: Float,
        config: BmiDialConfig
    ): Float {

        val ratio =
            ((bmi - config.minBmi) /
                    (config.maxBmi - config.minBmi))
                .coerceIn(0f, 1f)

        return -68.6f + ratio * 180f
    }

    private fun animateBmiValue(
        targetBmi: Float
    ) {

        bmiValueAnimator?.cancel()

        bmiValueAnimator = ValueAnimator.ofFloat(
            0f,
            targetBmi
        ).apply {

            duration = 800L

            addUpdateListener {
                val value = it.animatedValue as Float

                _binding?.bmiResult?.text =
                    String.format("%.1f", value)
            }

            start()
        }
    }//bmi数值动画

    private fun animatePointer(
        targetRotation: Float
    ) {
        pointerAnimator?.cancel()
        _binding?.BmiPointer?.rotation = -68.6f

        pointerAnimator = _binding?.BmiPointer
            ?.animate()
            ?.rotation(targetRotation)
            ?.setDuration(1200L)

        pointerAnimator?.start()
    }//指针动画

    private fun showResultWithAnimation(
        record: BmiRecord,
        config: BmiDialConfig
    ) {
        val bmi = record.bmi.toFloat()
        // BMI 从 0 开始
        binding.bmiResult.text = "0.0"
        // 指针从最左边开始
        binding.BmiPointer.rotation = -68.6f
        // BMI 数字动画
        animateBmiValue(bmi)
        // 指针动画
        val targetRotation =
            bmiToPointerRotation(bmi, config)
        animatePointer(targetRotation)
    }

    private fun getWeightRangeText(
        statusResult: BmiStatusResult
    ): String {
        return "${String.format("%.1f", statusResult.minHealthyWeight)}kg - " +
                "${String.format("%.1f", statusResult.maxHealthyWeight)}kg"
    }

    private fun getDifferenceText(
        statusResult: BmiStatusResult
    ): String? {

        val difference =
            statusResult.weightDifference
                ?: return null

        return when (statusResult.status) {

            BmiStatus.UNDERWEIGHT ->
                "(-${String.format("%.1f", difference)}kg)"

            BmiStatus.OVERWEIGHT ->
                "(+${String.format("%.1f", difference)}kg)"

            BmiStatus.NORMAL ->
                null
        }
    }

    private fun setAdviceText(record: BmiRecord)
    {
        val statusResult = viewModel.getStatus(record)//计算正常体重范围，以及与正常范围的差值
        val weightRange =
            getWeightRangeText(statusResult)//获取正常体重范围的文本

        val differenceText =
            getDifferenceText(statusResult)//获取与正常范围的差值文本
        val text = when (statusResult.status) {
            BmiStatus.UNDERWEIGHT,
            BmiStatus.OVERWEIGHT -> {
                if(record.heightUnit == "cm"){
                    getString(R.string.bmi_result_suggest_start,record.heightCm.toInt().toString() + "cm") +
                            "$weightRange $differenceText"
                } else {
                    val totalInches = (record.heightCm / 2.54).roundToInt()
                    val feet = totalInches / 12
                    val inches = totalInches % 12
                    getString(R.string.bmi_result_suggest_start, feet.toString() + "ft " + inches.toString() + "in") +
                            "$weightRange $differenceText"
                }
            }
            BmiStatus.NORMAL -> {
                if (!record.isChild)
                {
                    getString(R.string.bmi_range_normal_adult_description)
                }else{
                    getString(R.string.bmi_range_normal_child_description)
                }
            }
        }
        val spannable = SpannableString(text)

        val boldTypeface = ResourcesCompat.getFont(
            requireContext(),
            R.font.montserrat_extrabold
        ) ?: return

        // 正常体重范围
        val rangeStart = text.indexOf(weightRange)

        if (rangeStart != -1) {

            val rangeEnd =
                rangeStart + weightRange.length

            spannable.setSpan(
                CustomTypefaceSpan(boldTypeface),
                rangeStart,
                rangeEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 超重 / 体重不足的差值
        if (differenceText != null) {

            val differenceStart =
                text.indexOf(differenceText)

            if (differenceStart != -1) {

                val differenceEnd =
                    differenceStart + differenceText.length

                spannable.setSpan(
                    CustomTypefaceSpan(boldTypeface),
                    differenceStart,
                    differenceEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // 颜色
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.adviceWeight
                        )
                    ),
                    differenceStart,
                    differenceEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        binding.advice.text = spannable
    }


}