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
import com.example.bmi.ui.input.InputActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.math.roundToInt
import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bmi.ui.result.category.BmiStatus
import com.example.bmi.ui.result.category.BmiStatusResult
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment() {

    private val viewModel: ResultViewModel by activityViewModel()

    private lateinit var mode: ResultMode
    private var recordId: Long = -1L







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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.onIntent(
            ResultIntent.LoadRecord(
                mode = mode,
                recordId = recordId
            )
        )

        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                val uiState by
                viewModel.uiState.collectAsStateWithLifecycle()

                ResultScreen(
                    uiState = uiState,
                    mode = mode,
                    onIntent = viewModel::onIntent
                )
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    when (mode) {

                        ResultMode.NORMAL,
                        ResultMode.NEW_USER -> {
                            showConfirmDialog()
                        }

                        ResultMode.HISTORY -> {
                            requireActivity().finish()
                        }

                        ResultMode.LATEST -> {
                            isEnabled = false
                            requireActivity()
                                .onBackPressedDispatcher
                                .onBackPressed()
                            isEnabled = true
                        }
                    }
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.effect.collect { effect ->

                    when (effect) {

                        ResultEffect.DeleteAndGoToInput -> {

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
                        }

                        ResultEffect.DeleteAndFinish -> {

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
                        else -> {}
                    }
                }
            }
        }
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
                viewModel.deleteRecord(recordId)
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

    private fun getWeightRangeText(
        statusResult: BmiStatusResult
    ): String {
        return "${String.format(Locale.US,"%.1f", statusResult.minHealthyWeight)}kg - " +
                "${String.format(Locale.US,"%.1f", statusResult.maxHealthyWeight)}kg"
    }

    private fun getDifferenceText(
        statusResult: BmiStatusResult
    ): String? {

        val difference =
            statusResult.weightDifference
                ?: return null

        return when (statusResult.status) {

            BmiStatus.UNDERWEIGHT ->
                "(-${String.format(Locale.US,"%.1f", difference)}kg)"

            BmiStatus.OVERWEIGHT ->
                "(+${String.format(Locale.US,"%.1f", difference)}kg)"

            BmiStatus.NORMAL ->
                null
        }
    }

//    private fun setAdviceText(record: BmiRecord)
//    {
//        val statusResult = viewModel.getStatus(record)
//            ?: return//计算正常体重范围，以及与正常范围的差值
//        val weightRange =
//            getWeightRangeText(statusResult)//获取正常体重范围的文本
//
//        val differenceText =
//            getDifferenceText(statusResult)//获取与正常范围的差值文本
//        val text = when (statusResult.status) {
//            BmiStatus.UNDERWEIGHT,
//            BmiStatus.OVERWEIGHT -> {
//                if(record.heightUnit == "cm"){
//                    getString(R.string.bmi_result_suggest_start,record.heightCm.toInt().toString() + "cm") +
//                            "$weightRange $differenceText"
//                } else {
//                    val totalInches = (record.heightCm / 2.54).roundToInt()
//                    val feet = totalInches / 12
//                    val inches = totalInches % 12
//                    getString(R.string.bmi_result_suggest_start, feet.toString() + "ft " + inches.toString() + "in") +
//                            "$weightRange $differenceText"
//                }
//            }
//            BmiStatus.NORMAL -> {
//                if (!record.isChild)
//                {
//                    getString(R.string.bmi_range_normal_adult_description)
//                }else{
//                    getString(R.string.bmi_range_normal_child_description)
//                }
//            }
//        }
//        val spannable = SpannableString(text)
//
//        val boldTypeface = ResourcesCompat.getFont(
//            requireContext(),
//            R.font.montserrat_extrabold
//        ) ?: return
//
//        // 正常体重范围
//        val rangeStart = text.indexOf(weightRange)
//
//        if (rangeStart != -1) {
//
//            val rangeEnd =
//                rangeStart + weightRange.length
//
//            spannable.setSpan(
//                CustomTypefaceSpan(boldTypeface),
//                rangeStart,
//                rangeEnd,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
//
//        // 超重 / 体重不足的差值
//        if (differenceText != null) {
//
//            val differenceStart =
//                text.indexOf(differenceText)
//
//            if (differenceStart != -1) {
//
//                val differenceEnd =
//                    differenceStart + differenceText.length
//
//                spannable.setSpan(
//                    CustomTypefaceSpan(boldTypeface),
//                    differenceStart,
//                    differenceEnd,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//                // 颜色
//                spannable.setSpan(
//                    ForegroundColorSpan(
//                        ContextCompat.getColor(
//                            requireContext(),
//                            R.color.adviceWeight
//                        )
//                    ),
//                    differenceStart,
//                    differenceEnd,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//            }
//        }
//
//    }


}