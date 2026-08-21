package com.example.bmi.ui.statistics

import android.content.Context
import android.widget.TextView
import com.example.bmi.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.util.Locale
import com.github.mikephil.charting.data.Entry

class BmiMarkerView(
    context: Context,
    private val unit: String
) : MarkerView(
    context,
    R.layout.view_bmi_view
) {

    private val bmiValue =
        findViewById<TextView>(
            R.id.bmiValue
        )

    override fun refreshContent(
        e: Entry?,
        highlight: Highlight?
    ) {

        if (e == null) return

        bmiValue.text = String.format(
            Locale.US,
            "%.1f%s",
            e.y,
            unit
        )

        super.refreshContent(
            e,
            highlight
        )
    }

    override fun getOffset(): MPPointF {
        return MPPointF(
            -(width / 2f),
            -height.toFloat() - 10f
        )
    }
}