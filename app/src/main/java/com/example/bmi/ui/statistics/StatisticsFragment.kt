package com.example.bmi.ui.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bmi.ui.CustomPopup
import com.example.bmi.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue


/**
 * A simple [Fragment] subclass.
 * Use the [StatisticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatisticsFragment : Fragment() {
    private val viewModel: StatisticsViewModel by activityViewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var currentInterval by remember {
                    mutableStateOf(ChartInterval.DAY)
                }

                val dailyBmi by viewModel.dailyBmi.collectAsStateWithLifecycle()
                val dailyWeight by viewModel.dailyWeight.collectAsStateWithLifecycle()

                val weeklyBmi by viewModel.weeklyBmi.collectAsStateWithLifecycle()
                val weeklyWeight by viewModel.weeklyWeight.collectAsStateWithLifecycle()

                val monthlyBmi by viewModel.monthlyBmi.collectAsStateWithLifecycle()
                val monthlyWeight by viewModel.monthlyWeight.collectAsStateWithLifecycle()
                val timeMarkers by viewModel.timeMarkers.collectAsStateWithLifecycle()
                StatisticsScreen(
                    currentInterval = currentInterval,
                    onIntervalClick = { interval ->

                        currentInterval = interval

                        viewModel.setInterval(interval)

                    },
                    dailyBmi = dailyBmi,
                    dailyWeight = dailyWeight,
                    weeklyBmi = weeklyBmi,
                    weeklyWeight = weeklyWeight,
                    monthlyBmi = monthlyBmi,
                    monthlyWeight = monthlyWeight,
                    timeMarkers = timeMarkers,

                    onUpdateClick = {
                        (requireActivity() as MainActivity)
                            .goToInputPage()
                    }
                )
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStop() {
        CustomPopup.dismiss()
        super.onStop()
    }
}




