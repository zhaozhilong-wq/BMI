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
import kotlin.math.sin

class BmiDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val Float.dp: Float
        get() = this * resources.displayMetrics.density

    private var config: BmiDialConfig? = null

    fun setConfig(config: BmiDialConfig) {
        this.config = config
        invalidate()//如果view的内容改变，请重新调用ondraw
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE//只画线，不填充
        strokeWidth = 90f.dp//线粗细
        strokeCap = Paint.Cap.BUTT//线头形状
        color = Color.LTGRAY
    }//画笔
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
    }//画文字的画笔

    override fun onDraw(canvas: Canvas) {//canvas就是画布
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
        )//构造一个包住这个圆的矩形

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
                rect,//圆所在矩形
                startAngle,//从哪里开始
                sweepAngle,//转多少度
                false,//是否链接圆心
                paint//用哪个画笔
            )
        }
        drawTicks(
            canvas,
            config,
            centerX,
            centerY,
            radius
        )//画数字
    }

    private fun bmiToAngle(
        bmi: Float,
        config: BmiDialConfig
    ): Float {

        val ratio =
            (bmi - config.minBmi) /
                    (config.maxBmi - config.minBmi)

        return 180f + ratio * 180f
    }//计算比例
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
            val angleRadians = Math.toRadians(//角度转弧度
                angleDegrees.toDouble()
            )
            val x = centerX +
                    textRadius *
                    cos(angleRadians).toFloat()
            val y = centerY +
                    textRadius *
                    sin(angleRadians).toFloat()//计算数字坐标

            val text = if (bmi % 1f == 0f) {
                bmi.toInt().toString()
            } else {
                bmi.toString()
            }
            val fontMetrics = textPaint.fontMetrics
            val baselineY =
                -(fontMetrics.ascent + fontMetrics.descent) / 2f//让文字的中心落在坐标系原点

            canvas.save()

            canvas.translate(x, y)//移动画布坐标系原点

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
