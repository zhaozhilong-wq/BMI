package com.example.bmi.ui.input

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bmi.R
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

    private val viewModel : InputViewModel by activityViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(
            DatePickerDialog.REQUEST_KEY,
            this
        ) { _, bundle ->

            val year = bundle.getInt(DatePickerDialog.KEY_YEAR)
            val month = bundle.getInt(DatePickerDialog.KEY_MONTH)
            val day = bundle.getInt(DatePickerDialog.KEY_DAY)

            viewModel.selectDate(
                year = year,
                month = month,
                day = day
            )
        }

        parentFragmentManager.setFragmentResultListener(
            TimePickerDialog.REQUEST_KEY,
            this
        ) { _, bundle ->

            val timeSlot = bundle.getInt(
                TimePickerDialog.KEY_TIME_SLOT
            )

            viewModel.selectTimeSlot(timeSlot)
        }
    }

    override fun onStop() {
        CustomPopup.dismiss()
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                InputScreen(
                    uiState = uiState,

                    onWeightChanged = viewModel::onWeightChanged,
                    onWeightFocusChanged = viewModel::onWeightFocusChanged,
                    onWeightUnitSelected = viewModel::selectWeightUnit,

                    onHeightCmChanged = viewModel::onHeightCmChanged,
                    onHeightFtChanged = viewModel::onHeightFtChanged,
                    onHeightInChanged = viewModel::onHeightInChanged,

                    onHeightCmFocusChanged =
                        viewModel::onHeightCmFocusChanged,

                    onHeightFtFocusChanged =
                        viewModel::onHeightFtFocusChanged,

                    onHeightInFocusChanged =
                        viewModel::onHeightInFocusChanged,

                    onHeightUnitSelected =
                        viewModel::selectHeightUnit,

                    onDateClick = {
                        DatePickerDialog.newInstance(
                            year = uiState.year,
                            month = uiState.month,
                            day = uiState.day
                        ).show(
                            parentFragmentManager,
                            "DatePickerDialog"
                        )
                    },

                    onTimeClick = {

                        TimePickerDialog.newInstance(
                            selectedTime = uiState.timeSlot
                        ).show(
                            parentFragmentManager,
                            "TimePickerDialog"
                        )
                    },

                    onAgeSelected = viewModel::selectAge,

                    onGenderSelected = viewModel::selectGender,

                    onCalculateClick =
                        viewModel::calculateAndSave,

                    onUserClick = {
                        val intent = Intent(requireContext(), SettingActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }

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
                launch {
                    viewModel.resultReady.collect { (mode, recordId) ->
                        resultLauncher.launch(
                            ResultActivity.newIntent(
                                requireContext(),
                                mode,
                                recordId
                            )
                        )
                    }
                }

                launch {
                    viewModel.toastEvent.collect { (resId, range) ->

                        CustomPopup.show(
                            requireContext(),
                            requireActivity().window.decorView,
                            getString(resId, range),
                            R.drawable.warning_icon
                        )
                    }
                }
            }
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

                if (
                    !deleteSuccess ||
                    !isAdded ||
                    view == null
                ) {
                    return@registerForActivityResult
                }

                if (deleteSuccess ) {
                    CustomPopup.show(
                        requireContext(),
                        requireActivity().window.decorView,
                        getString(R.string.delete_successfully),
                        R.drawable.success_icon
                    )
                }
            }
        }




}