package com.example.bmi.ui.result

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.bmi.R
import com.example.bmi.databinding.DialogConfirmBinding

class ConfirmDialog: DialogFragment() {

    private var _binding: DialogConfirmBinding? = null
    private val binding get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(
            requireContext(),
            R.style.DatePickerDialogStyle
        )

        _binding = DialogConfirmBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.delete.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    RESULT_KEY to true
                )
            )
            dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(
                android.R.color.transparent
            )
            val margin = dp(37)
            val screenWidth =
                resources.displayMetrics.widthPixels
            val dialogWidth =
                screenWidth - margin * 2
            setLayout(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun dp(value: Int): Int {
        return (
                value * resources.displayMetrics.density
                ).toInt()
    }

    companion object {

        const val TAG = "ConfirmDialog"

        const val REQUEST_KEY = "confirm_dialog_result"
        const val RESULT_KEY = "delete_confirmed"
    }
}