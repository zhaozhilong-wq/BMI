package com.example.bmi.ui.input

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

    private val viewModel : InputViewModel by activityViewModel()

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                launch {
                    viewModel.effect.collect { effect ->

                        when (effect) {

                            is InputEffect.ShowToast -> {
                                CustomPopup.show(
                                    requireContext(),
                                    requireActivity().window.decorView,
                                    getString(
                                        effect.resId,
                                        effect.range
                                    ),
                                    R.drawable.warning_icon
                                )
                            }

                            is InputEffect.NavigateToResult -> {
                                resultLauncher.launch(
                                    ResultActivity.newIntent(
                                        requireContext(),
                                        effect.mode,
                                        effect.recordId
                                    )
                                )
                            }

                            InputEffect.NavigateToSetting -> {
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        SettingActivity::class.java
                                    )
                                )
                            }
                        }
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