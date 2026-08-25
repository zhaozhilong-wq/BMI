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

                    val config = viewModel.getDialConfig(record)
                    binding.bmiDialView.setConfig(config)
                    if (record.isChild) {

                        val childThreshold = viewModel.getChildThreshold(record)
                        if (childThreshold == null) {
                            Log.e(
                                "ResultFragment",
                                "No BMI threshold found: gender=${record.gender}, age=${record.age}"
                            )
                            return@collect
                        }
                        val category = viewModel.getCategory(record)
                        setupChildCategories(childThreshold,
                            selectedCategory = category)
                        binding.bmiStatus.text = getString(category.displayName)
                        if (category.displayName == R.string.bmi_obese_class_i){
                            binding.bmiStatus.text = "Obese"
                        }
                        setStatusBackground(category.colorRes)

                        val statusResult = viewModel.getStatus(record)

                        val weightRange =
                            getWeightRangeText(statusResult)

                        val differenceText =
                            getDifferenceText(statusResult)

                        val text = when (statusResult.status) {
                            BmiStatus.UNDERWEIGHT,
                            BmiStatus.OVERWEIGHT -> {
                                "Normal Weight for your height (${record.heightCm.toInt()}cm):\n" +
                                        "$weightRange $differenceText"
                            }

                            BmiStatus.NORMAL -> {
                                "\uD83D\uDC4D Thumbs Up! You’ve done a great job and now only need to keep your lifestyle healthy to stay in this range."
                            }
                        }

                        setBmiAdviceText(
                            text = text,
                            weightRange = weightRange,
                            differenceText = differenceText
                        )

                    } else {
                        // 成年人显示分类
                        val category = viewModel.getCategory(record)
                        setupAdultCategories(category)
                        binding.bmiStatus.text = getString(category.displayName)
                        setStatusBackground(category.colorRes)
                        val statusResult = viewModel.getStatus(record)
                        val weightRange =
                            getWeightRangeText(statusResult)

                        val differenceText =
                            getDifferenceText(statusResult)
                        val text = when (statusResult.status) {
                            BmiStatus.UNDERWEIGHT,
                            BmiStatus.OVERWEIGHT -> {
                                "Normal Weight for your height (${record.heightCm.toInt()}cm):\n" +
                                        "$weightRange $differenceText"
                            }
                            BmiStatus.NORMAL -> {
                                "😎 Congratulations! You’re in a great place now. " +
                                        "Keep up your healthy habits to maintain your healthy weight."
                            }
                        }

                        setBmiAdviceText(
                            text = text,
                            weightRange = weightRange,
                            differenceText = differenceText
                        )
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
                        requireActivity().finish()
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
        }

    }

    override fun onDestroyView() {
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

    private fun setBmiAdviceText(
        text: String,
        weightRange: String,
        differenceText: String?
    ) {

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

    private fun animateBmiValue(
        targetBmi: Float
    ) {
        val animator = ValueAnimator.ofFloat(0f, targetBmi)

        animator.duration = 800L

        animator.addUpdateListener {
            val value = it.animatedValue as Float

            binding.bmiResult.text =
                String.format("%.1f", value)
        }

        animator.start()
    }//bmi数值动画

    private fun animatePointer(
        targetRotation: Float
    ) {
        binding.BmiPointer.rotation = -71f

        binding.BmiPointer.animate()
            .rotation(targetRotation)
            .setDuration(1200L)
            .start()
    }//指针动画

    private fun showResultWithAnimation(
        record: BmiRecord,
        config: BmiDialConfig
    ) {
        val bmi = record.bmi.toFloat()
        // BMI 从 0 开始
        binding.bmiResult.text = "0.0"
        // 指针从最左边开始
        binding.BmiPointer.rotation = -71f
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


}