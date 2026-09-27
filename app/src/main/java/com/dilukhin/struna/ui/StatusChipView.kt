package com.dilukhin.struna.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.dilukhin.struna.R

class StatusChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    private val dot = View(context)
    private val label = TextView(context)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        minimumHeight = context.dp(30f)
        setPadding(context.dp(11f), context.dp(5f), context.dp(11f), context.dp(5f))
        background = context.roundedBackground(
            R.color.struna_color_accent_soft,
            20f,
            R.color.struna_color_border,
        )

        dot.layoutParams = LayoutParams(context.dp(8f), context.dp(8f)).apply {
            marginEnd = context.dp(8f)
        }
        dot.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(context.getColor(R.color.struna_color_accent))
        }
        addView(dot)

        label.configureSingleLineText(
            R.dimen.struna_text_cents,
            R.color.struna_color_text_primary,
            medium = true,
            gravityValue = Gravity.CENTER,
        )
        addView(label, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

        setStatus(R.string.status_offline, showDot = true)
    }

    fun setStatus(textRes: Int, showDot: Boolean) {
        label.setText(textRes)
        dot.visibility = if (showDot) VISIBLE else GONE
        contentDescription = label.text
    }
}
