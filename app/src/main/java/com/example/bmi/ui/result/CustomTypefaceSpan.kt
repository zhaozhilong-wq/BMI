package com.example.bmi.ui.result

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomTypefaceSpan(//用来设置某段文字的字体
    private val typeface: Typeface
) : MetricAffectingSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.typeface = typeface
    }

    override fun updateMeasureState(paint: TextPaint) {
        paint.typeface = typeface
    }
}