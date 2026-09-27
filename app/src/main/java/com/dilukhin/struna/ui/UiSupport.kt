package com.dilukhin.struna.ui

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

internal fun Context.dp(value: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics).roundToInt()

internal fun TextView.useTextSize(dimenRes: Int) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(dimenRes))
}

internal fun TextView.useRegularTypeface() {
    typeface = Typeface.create("sans-serif", Typeface.NORMAL)
}

internal fun TextView.useMediumTypeface() {
    typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
}

internal fun TextView.useBoldTypeface() {
    typeface = Typeface.create("sans-serif", Typeface.BOLD)
}

internal fun TextView.configureSingleLineText(
    dimenRes: Int,
    colorRes: Int,
    medium: Boolean = false,
    gravityValue: Int = Gravity.CENTER_VERTICAL,
) {
    includeFontPadding = false
    maxLines = 1
    gravity = gravityValue
    useTextSize(dimenRes)
    setTextColor(context.getColor(colorRes))
    if (medium) useMediumTypeface() else useRegularTypeface()
}

internal fun Context.roundedBackground(
    fillColorRes: Int,
    radiusDp: Float,
    strokeColorRes: Int? = null,
    strokeWidthDp: Float = 1f,
): GradientDrawable = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    setColor(getColor(fillColorRes))
    cornerRadius = dp(radiusDp).toFloat()
    if (strokeColorRes != null) {
        setStroke(dp(strokeWidthDp).coerceAtLeast(1), getColor(strokeColorRes))
    }
}

internal fun View.setFixedSize(widthDp: Float, heightDp: Float) {
    layoutParams = LinearLayout.LayoutParams(context.dp(widthDp), context.dp(heightDp))
}

internal fun ViewGroup.addGap(heightDp: Float) {
    addView(View(context), ViewGroup.LayoutParams(1, context.dp(heightDp)))
}
