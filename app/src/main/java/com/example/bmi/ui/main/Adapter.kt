package com.example.bmi.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bmi.ui.input.InputFragment
import com.example.bmi.ui.result.ResultFragment

class Adapter (activity: MainActivity): FragmentStateAdapter(activity)
{
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position : Int): Fragment {
        return when(position){
            0-> InputFragment()
            1-> ResultFragment()
            else-> InputFragment()
        }

    }
}