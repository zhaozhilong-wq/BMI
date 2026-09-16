package com.example.bmi.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.bmi.R
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            )//根据bmi数值计算他的位置
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
fun bmiToAngle(
    bmi: Float,
    config: BmiDialConfig
): Float {

    val ratio =
        (bmi - config.minBmi) /
                (config.maxBmi - config.minBmi)

    return 180f + ratio * 180f
}


@Composable
fun BmiDialView(
    config: BmiDialConfig?,
    modifier: Modifier = Modifier
) {

    if (config == null) {
        return
    }

    val density = LocalDensity.current


    val fontFamily = remember {
        FontFamily(
            Font(R.font.montserrat_extrabold)
        )
    }

    // Compose 画文字需要 TextMeasurer。
    val textMeasurer = rememberTextMeasurer()

    val strokeWidth = with(density) {
        90.dp.toPx()
    }


    val radius = with(density) {
        115.dp.toPx()
    }

    val textRadius = radius + with(density) {
        55.dp.toPx()
    }
    val sectionColors = config.sections.map { section ->
        colorResource(section.color)
    }

    Canvas(
        modifier = modifier
    ) {

        val centerX = size.width / 2f
        val centerY = size.height


        drawSections(
            config = config,
            centerX = centerX,
            centerY = centerY,
            radius = radius,
            strokeWidth = strokeWidth,
            sectionColors = sectionColors
        )


        drawTicks(
            config = config,
            centerX = centerX,
            centerY = centerY,
            textRadius = textRadius,
            textMeasurer = textMeasurer,
            fontFamily = fontFamily
        )
    }
}



private fun DrawScope.drawSections(
    config: BmiDialConfig,
    centerX: Float,
    centerY: Float,
    radius: Float,
    sectionColors: List<androidx.compose.ui.graphics.Color>,
    strokeWidth: Float
) {

    val topLeft = Offset(
        x = centerX - radius,
        y = centerY - radius
    )

    val arcSize = Size(
        width = radius * 2f,
        height = radius * 2f
    )

    for ((index, section) in config.sections.withIndex()) {

        val startAngle = bmiToAngle(
            section.min,
            config
        )

        val endAngle = bmiToAngle(
            section.max,
            config
        )

        val sweepAngle =
            endAngle - startAngle

        drawArc(
            color = sectionColors[index],
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = strokeWidth
            )
        )
    }
}



private fun DrawScope.drawTicks(
    config: BmiDialConfig,
    centerX: Float,
    centerY: Float,
    textRadius: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    fontFamily: FontFamily
) {

    val textStyle = TextStyle(
        color = androidx.compose.ui.graphics.Color.Black,
        fontSize = 10.sp,
        fontFamily = fontFamily,
        letterSpacing = (-0.01).sp
    )

    for (bmi in config.ticks) {

        val angleDegrees = bmiToAngle(
            bmi,
            config
        )

        //角度转弧度
        val angleRadians =
            Math.toRadians(
                angleDegrees.toDouble()
            )


        val x =
            centerX +
                    textRadius *
                    cos(angleRadians).toFloat()

        val y =
            centerY +
                    textRadius *
                    sin(angleRadians).toFloat()

        // BMI 是整数就不显示 .0
        val text =
            if (bmi % 1f == 0f) {
                bmi.toInt().toString()
            } else {
                bmi.toString()
            }


        val textLayoutResult =
            textMeasurer.measure(
                text = text,
                style = textStyle
            )


        val textWidth =
            textLayoutResult.size.width

        val textHeight =
            textLayoutResult.size.height


        rotate(
            degrees = angleDegrees - 270f,
            pivot = Offset(x, y)
        ) {

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = x - textWidth / 2f,
                    y = y - textHeight / 2f
                )
            )
        }
    }
}


