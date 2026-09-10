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
import android.app.framework.base.collectEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bmi.ui.main.MainActivity
import com.example.bmi.ui.recent.RecentActivity
import com.example.bmi.ui.result.category.BmiStatus
import com.example.bmi.ui.result.category.BmiStatusResult
import java.util.Locale
import kotlin.jvm.java


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

        viewModel.dispatch(
            ResultEvent.LoadRecord(
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

                viewModel.collectEffect { effect ->

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

                ResultScreen(
                    uiState = uiState,
                    mode = mode,
                    dispatch = viewModel::dispatch,
                    onBack = {requireActivity().finish()},
                    onRecent = {
                        startActivity(Intent(requireContext(), RecentActivity::class.java))
                    },
                    onBackgroundClick = {
                        (requireActivity() as MainActivity).goToInputPage()
                    },
                    onSave = {
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
                )
            }
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
}