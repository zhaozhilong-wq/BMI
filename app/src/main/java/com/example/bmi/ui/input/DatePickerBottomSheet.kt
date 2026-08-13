package com.example.bmi.ui.input

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bmi.databinding.DialogDatePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DatePickerBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogDatePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogDatePickerBinding.inflate(
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
}