package com.dilukhin.struna.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.dilukhin.struna.R
import kotlin.math.max
import kotlin.math.min

class CentsGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : android.view.View(context, attrs) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.struna_text_scale)
    }
    private var cents: Double? = null
    private var displayState: TuningDisplayState = TuningDisplayState.WAITING

    init {
        minimumHeight = context.dp(73f)
    }

    fun setReading(cents: Double?, displayState: TuningDisplayState) {
        this.cents = cents
        this.displayState = displayState
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val gaugeHeight = context.dp(50f).toFloat()
        val left = context.dp(10f).toFloat()
        val right = width - context.dp(10f).toFloat()
        val centerY = context.dp(25f).toFloat()
        val tickHalf = context.dp(6f).toFloat()

        linePaint.color = context.getColor(R.color.struna_color_border)
        linePaint.strokeWidth = context.dp(3f).toFloat()
        linePaint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(left, centerY, right, centerY, linePaint)

        linePaint.color = context.getColor(R.color.struna_color_text_secondary)
        linePaint.strokeWidth = context.dp(2f).toFloat()
        val ticks = floatArrayOf(0f, 0.25f, 0.5f, 0.75f, 1f)
        for (fraction in ticks) {
            val x = left + (right - left) * fraction
            canvas.drawLine(x, centerY - tickHalf, x, centerY + tickHalf, linePaint)
        }

        centerPaint.color = context.getColor(R.color.struna_color_accent)
        centerPaint.strokeWidth = context.dp(3f).toFloat()
        centerPaint.strokeCap = Paint.Cap.ROUND
        val centerX = (left + right) / 2f
        canvas.drawLine(
            centerX,
            centerY - context.dp(10f),
            centerX,
            centerY + context.dp(10f),
            centerPaint,
        )

        cents?.let { raw ->
            val clamped = max(-50.0, min(50.0, raw))
            val fraction = ((clamped + 50.0) / 100.0).toFloat()
            val x = left + (right - left) * fraction
            pointerPaint.color = when (displayState) {
                TuningDisplayState.FLAT -> context.getColor(R.color.struna_color_low)
                TuningDisplayState.SHARP -> context.getColor(R.color.struna_color_high)
                TuningDisplayState.UNSTABLE ->
                    if (raw < 0.0) context.getColor(R.color.struna_color_low)
                    else context.getColor(R.color.struna_color_high)
                else -> context.getColor(R.color.struna_color_accent)
            }
            val top = context.dp(4f).toFloat()
            val bottom = context.dp(18f).toFloat()
            val halfWidth = context.dp(9f).toFloat()
            val path = Path().apply {
                moveTo(x - halfWidth, top)
                lineTo(x + halfWidth, top)
                lineTo(x, bottom)
                close()
            }
            canvas.drawPath(path, pointerPaint)
        }

        labelPaint.color = context.getColor(R.color.struna_color_text_secondary)
        val labels = arrayOf("-50", "-25", "0", "+25", "+50")
        val labelBaseline = gaugeHeight + context.dp(14f)
        labels.forEachIndexed { index, label ->
            val x = left + (right - left) * ticks[index]
            canvas.drawText(label, x, labelBaseline, labelPaint)
        }
    }
}
