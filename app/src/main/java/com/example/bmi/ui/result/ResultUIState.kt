package com.example.bmi.ui.result

import android.app.framework.base.State
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.result.category.BmiCategory
import com.example.bmi.ui.result.category.BmiStatusResult
import com.example.bmi.ui.result.category.ChildBmiThreshold
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultUiState(
    val record: BmiRecord? = null,
    val category: BmiCategory? = null,
    val childThreshold: ChildBmiThreshold? = null,
    val dialConfig: BmiDialConfig? = null,
    val statusResult: BmiStatusResult? = null
) : State