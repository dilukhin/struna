package com.dilukhin.struna.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.dilukhin.struna.R

class StabilityCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    private val signalView = SignalStrengthView(context)
    private val level = TextView(context)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        minimumHeight = context.dp(84f)
        setPadding(context.dp(18f), context.dp(16f), context.dp(18f), context.dp(16f))
        background = context.roundedBackground(
            R.color.struna_color_surface,
            20f,
            R.color.struna_color_border,
        )

        addView(signalView, LayoutParams(context.dp(44f), context.dp(44f)).apply {
            marginEnd = context.dp(14f)
        })

        val texts = LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.START
        }
        val title = TextView(context).apply {
            configureSingleLineText(
                R.dimen.struna_text_cents,
                R.color.struna_color_text_primary,
                medium = true,
            )
            setText(R.string.stability_title)
        }
        level.configureSingleLineText(
            R.dimen.struna_text_cents,
            R.color.struna_color_text_secondary,
        )
        texts.addView(title, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        texts.addView(level, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            topMargin = context.dp(3f)
        })
        addView(texts, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        setQuality(SignalQuality.NONE)
    }

    fun setQuality(quality: SignalQuality) {
        signalView.quality = quality
        level.setText(
            when (quality) {
                SignalQuality.HIGH -> R.string.signal_high
                SignalQuality.LOW -> R.string.signal_low
                SignalQuality.NONE -> R.string.signal_none
            },
        )
    }
}

private class SignalStrengthView(context: Context) : View(context) {
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.struna_color_accent)
    }

    var quality: SignalQuality = SignalQuality.NONE
        set(value) {
            field = value
            invalidate()
        }

    init {
        background = context.roundedBackground(R.color.struna_color_accent_soft, 14f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val scaleX = width / 44f
        val scaleY = height / 44f
        val count = when (quality) {
            SignalQuality.HIGH -> 4
            SignalQuality.LOW -> 2
            SignalQuality.NONE -> 0
        }
        val heights = floatArrayOf(10f, 15f, 20f, 25f)
        val tops = floatArrayOf(25f, 22.5f, 20f, 17.5f)
        val xs = floatArrayOf(8f, 16f, 24f, 32f)
        for (i in 0 until count) {
            val left = xs[i] * scaleX
            val top = tops[i] * scaleY
            val right = (xs[i] + 5f) * scaleX
            val bottom = (tops[i] + heights[i]) * scaleY
            canvas.drawRoundRect(left, top, right, bottom, 2.5f * scaleX, 2.5f * scaleY, barPaint)
        }
    }
}
