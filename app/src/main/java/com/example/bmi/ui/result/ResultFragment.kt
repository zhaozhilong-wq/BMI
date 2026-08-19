package com.example.bmi.ui.result

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.example.bmi.ui.BmiSection
import com.example.bmi.ui.input.InputActivity
import com.example.bmi.ui.main.MainActivity
import com.example.bmi.ui.result.category.BmiCategory
import com.example.bmi.ui.result.category.BmiCategoryItem
import com.example.bmi.ui.result.category.BmiCategoryViewHelper
import com.example.bmi.ui.result.category.BmiCategoryViewHelper.createAdultCategoryItems
import com.example.bmi.ui.result.category.BmiCategoryViewHelper.createChildCategoryItems
import com.example.bmi.ui.result.category.ChildBmiThreshold
import com.example.bmi.ui.result.category.femaleChildBmi
import com.example.bmi.ui.result.category.maleChildBmi
import com.example.bmi.ui.toDialConfig
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.math.roundToInt


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
                    binding.bmiResult.text = record.bmi.toString()
                    binding.bmiDialView.setConfig(
                        viewModel.getDialConfig(record)
                    )
                    if (record.isChild) {

                        val childThreshold = viewModel.getChildThreshold(record)
                        if (childThreshold == null) {
                            Log.e(
                                "ResultFragment",
                                "No BMI threshold found: gender=${record.gender}, age=${record.age}"
                            )
                            return@collect
                        }
                        val category = viewModel.getChildCategory(
                            bmi = record.bmi.toFloat(),
                            threshold = childThreshold
                        )
                        setupChildCategories(childThreshold,
                            selectedCategory = category)
                        binding.bmiStatus.text = category.displayName
                        if (category.displayName == "Obese Class I"){
                            val display = "Obese"
                            binding.bmiStatus.text = display
                        }
                        setStatusBackground(category.colorRes)

                        val statusMessage =
                            viewModel.getChildStatusMessage(
                                record = record,
                                threshold = childThreshold
                            )

                        setBmiAdviceText(
                            text = statusMessage.text,
                            weightRange = statusMessage.weightRange,
                            differenceText = statusMessage.differenceText
                        )

                    } else {
                        // 成年人显示分类
                        val category = viewModel.getAdultCategory(record.bmi.toFloat())
                        setupAdultCategories(category)
                        binding.bmiStatus.text = category.displayName
                        setStatusBackground(category.colorRes)
                        val statusMessage =
                            viewModel.getAdultStatusMessage(
                                record = record
                            )

                        setBmiAdviceText(
                            text = statusMessage.text,
                            weightRange = statusMessage.weightRange,
                            differenceText = statusMessage.differenceText
                        )
                    }

                    setPersonInfo(record)

                    binding.BmiPointer.post {

                        binding.BmiPointer.pivotX =
                            binding.BmiPointer.width / 2f

                        binding.BmiPointer.pivotY =
                            binding.BmiPointer.height.toFloat()

                        binding.BmiPointer.rotation = bmiToPointerRotation(
                            record.bmi.toFloat(),
                            viewModel.getDialConfig(record)
                        )
                    }

                }
            }
        }

        binding.bmiHelp.setOnClickListener {
            currentRecord?.let { record ->
                val dialog = BmiDialDialog(
                    requireContext(),
                    record
                )
                dialog.show()
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
                }
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showConfirmDialog() {
        val dialog = ConfirmDialog(requireContext()) {
            viewModel.deleteRecord(recordId)
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isNewUser.collect { isNewUser ->
                        val targetActivity =
                            if (isNewUser) {
                                InputActivity::class.java
                            } else {
                                MainActivity::class.java
                            }
                        startActivity(
                            Intent(
                                requireContext(),
                                targetActivity
                            )
                        )
                        requireActivity().finish()
                    }
                }
            }
        }
        dialog.show()
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
        val lb = record.weightKg / 0.45359237
        if(record.weightUnit == "kg"){
            if (record.heightUnit == "cm"){
                binding.personInfo.text = "${record.weightKg} kg | ${record.heightCm} cm | ${record.gender} | ${record.age} years old"
            }else{
                binding.personInfo.text = "${record.weightKg} kg | ${feet}ft ${inches}in | ${record.gender} | ${record.age} years old"
            }

        }else{
            if(record.heightUnit == "cm"){
                binding.personInfo.text = "$lb lb | ${record.heightCm} cm | ${record.gender} | ${record.age} years old"
            }else{
                binding.personInfo.text = "$lb lb | ${feet}ft ${inches}in | ${record.gender} | ${record.age} years old"
            }
        }
    }

    private fun bmiToPointerRotation(
        bmi: Float,
        config: BmiDialConfig
    ): Float {

        val ratio =
            ((bmi - config.minBmi) /
                    (config.maxBmi - config.minBmi))
                .coerceIn(0f, 1f)

        return -71f + ratio * 180f
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


}