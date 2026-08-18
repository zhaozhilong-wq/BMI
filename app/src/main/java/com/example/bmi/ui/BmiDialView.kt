package com.example.bmi.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.bmi.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class   BmiDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val Float.dp: Float
        get() = this * resources.displayMetrics.density

    private var config: BmiDialConfig? = null

    fun setConfig(config: BmiDialConfig) {
        this.config = config
        invalidate()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 90f.dp
        strokeCap = Paint.Cap.BUTT
        color = Color.LTGRAY
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        textSize = 10f.dp
        textAlign = Paint.Align.CENTER
        letterSpacing=-0.01f
        typeface = ResourcesCompat.getFont(
            context,
            R.font.montserrat_extrabold
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val config = config ?: return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val centerX = viewWidth / 2f
        val centerY = viewHeight

        val radius = 115f.dp

        val rect = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        for (section in config.sections) {

            val startAngle = bmiToAngle(
                section.min,
                config
            )

            val endAngle = bmiToAngle(
                section.max,
                config
            )

            val sweepAngle = endAngle - startAngle

            paint.color = context.getColor(section.color)

            canvas.drawArc(
                rect,
                startAngle,
                sweepAngle,
                false,
                paint
            )
        }
        drawTicks(
            canvas,
            config,
            centerX,
            centerY,
            radius
        )
    }

    private fun bmiToAngle(
        bmi: Float,
        config: BmiDialConfig
    ): Float {

        val ratio =
            (bmi - config.minBmi) /
                    (config.maxBmi - config.minBmi)

        return 180f + ratio * 180f
    }
    private fun drawTicks(
        canvas: Canvas,
        config: BmiDialConfig,
        centerX: Float,
        centerY: Float,
        radius: Float
    ) {
        val textRadius = radius + 55f.dp
        for (bmi in config.ticks) {
            val angleDegrees = bmiToAngle(
                bmi,
                config
            )
            val angleRadians = Math.toRadians(
                angleDegrees.toDouble()
            )
            val x = centerX +
                    textRadius *
                    cos(angleRadians).toFloat()
            val y = centerY +
                    textRadius *
                    sin(angleRadians).toFloat()

            val text = if (bmi % 1f == 0f) {
                bmi.toInt().toString()
            } else {
                bmi.toString()
            }
            val fontMetrics = textPaint.fontMetrics
            val baselineY =
                -(fontMetrics.ascent + fontMetrics.descent) / 2f

            canvas.save()

            canvas.translate(x, y)

            val rotation =
                (angleDegrees - 270f)

            canvas.rotate(rotation)

            canvas.drawText(
                text,
                0f,
                baselineY,
                textPaint
            )

            canvas.restore()
        }
    }
}
