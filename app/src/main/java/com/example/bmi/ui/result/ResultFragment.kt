package com.example.bmi.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bmi.R
import com.example.bmi.databinding.FragmentResultBinding
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.BmiSection


/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val adultMaleConfig = BmiDialConfig(
        minBmi = 15.6f,
        maxBmi = 40.3f,
        sections = listOf(
            BmiSection(15.6f, 16f, R.color.vsu),
            BmiSection(16f, 17f, R.color.su),
            BmiSection(17f, 18.5f, R.color.underweight),
            BmiSection(18.5f, 25f, R.color.normal),
            BmiSection(25f, 30f, R.color.overweight),
            BmiSection(30f, 35f, R.color.obesity1),
            BmiSection(35f, 40f, R.color.obesity2),
            BmiSection(40f, 40.3f, R.color.obesity3)
        ),
        ticks = listOf(
            16f,
            17f,
            18.5f,
            25f,
            30f,
            35f,
            40f
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding.bmiDialView.setConfig(adultMaleConfig)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}