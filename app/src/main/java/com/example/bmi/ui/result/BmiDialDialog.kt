package com.example.bmi.ui.result

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.databinding.DialogBmiDialBinding
import com.example.bmi.ui.result.category.BmiCategoryViewHelper
import com.example.bmi.ui.result.category.BmiClassifier
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class BmiDialDialog: DialogFragment() {

    private var _binding: DialogBmiDialBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResultViewModel by activityViewModel()

    private var recordId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recordId = requireArguments().getLong(ARG_RECORD_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogBmiDialBinding.inflate(
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
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.record.collect { record ->

                    if (record == null) return@collect

                    if (record.id != recordId) return@collect

                    setupContent(record)
                }
            }
        }
    }

    private fun setupContent(record: BmiRecord) {

        viewModel.getDialConfig(record)?.let {
            binding.bmiDialView.setConfig(
                it
            )
        }

        setupCategories(record)

        binding.gotButton.setOnClickListener {
            dismiss()
        }

        if (record.isChild) {

            binding.title.text =
                getString(R.string.bmi_teenager_tip)

            val gender = if (record.gender == "Male") {
                getString(R.string.gender_boy)
            } else {
                getString(R.string.gender_girl)
            }

            binding.subTitle.text = getString(
                R.string.bmi_teenager_info_tip,
                record.age.toString(),
                gender
            )

            binding.subTitle.visibility = View.VISIBLE

        } else {

            binding.title.text =
                getString(R.string.bmi_adult_tip)

            binding.subTitle.visibility = View.GONE
        }
    }

    private fun setupCategories(record: BmiRecord) {

        val container =
            binding.bmiCategoryLayout.bmiCategoryContainer

        val selectedCategory =
            BmiClassifier.classify(record)

        if (record.isChild) {

            val threshold =
                BmiClassifier.getChildThreshold(record)
                    ?: return

            val items =
                BmiCategoryViewHelper.createChildCategoryItems(
                    threshold
                )

            BmiCategoryViewHelper.setup(
                container = container,
                items = items,
                selectedCategory = selectedCategory,
                inflater = layoutInflater
            )

        } else {

            val items =
                BmiCategoryViewHelper.createAdultCategoryItems()

            BmiCategoryViewHelper.setup(
                container = container,
                items = items,
                selectedCategory = selectedCategory,
                inflater = layoutInflater
            )
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
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_RECORD_ID = "arg_record_id"

        fun newInstance(recordId: Long): BmiDialDialog {
            return BmiDialDialog().apply {

                arguments = Bundle().apply {
                    putLong(ARG_RECORD_ID, recordId)
                }
            }
        }
    }

}