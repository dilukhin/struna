package com.dilukhin.struna.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.dilukhin.struna.R

class IconButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : TextView(context, attrs) {
    init {
        gravity = Gravity.CENTER
        includeFontPadding = false
        useRegularTypeface()
        setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 24f)
        setTextColor(context.getColor(R.color.struna_color_text_primary))
        minWidth = context.dp(48f)
        minHeight = context.dp(48f)
    }

    fun setSymbol(symbol: CharSequence, descriptionRes: Int) {
        text = symbol
        contentDescription = context.getString(descriptionRes)
    }
}
