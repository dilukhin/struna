package com.dilukhin.struna.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.dilukhin.struna.R

enum class ActionButtonStyle {
    PRIMARY,
    SECONDARY,
}

class ActionButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : TextView(context, attrs) {
    init {
        gravity = Gravity.CENTER
        includeFontPadding = false
        useMediumTypeface()
        setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15f)
        minHeight = context.dp(48f)
        setPadding(context.dp(18f), context.dp(12f), context.dp(18f), context.dp(12f))
    }

    fun configure(labelRes: Int, style: ActionButtonStyle) {
        setText(labelRes)
        when (style) {
            ActionButtonStyle.PRIMARY -> {
                background = context.roundedBackground(R.color.struna_color_accent, 16f)
                setTextColor(context.getColor(R.color.struna_color_surface))
            }
            ActionButtonStyle.SECONDARY -> {
                background = context.roundedBackground(
                    R.color.struna_color_surface,
                    16f,
                    R.color.struna_color_border,
                )
                setTextColor(context.getColor(R.color.struna_color_text_primary))
            }
        }
    }
}
