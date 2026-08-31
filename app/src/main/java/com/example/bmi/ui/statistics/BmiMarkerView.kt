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

    override fun refreshContent(//根据选中的数据点，刷新view中的内容
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

    override fun getOffset(): MPPointF {//显示的位置，
        return MPPointF(
            -(width / 2f),//水平中心对准数据点
            -height.toFloat() - 10f//向上移到自身高度+10dp
        )
    }
}