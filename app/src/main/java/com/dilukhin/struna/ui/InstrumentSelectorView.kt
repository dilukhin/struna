package com.dilukhin.struna.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.dilukhin.struna.R
import com.dilukhin.struna.domain.Instrument

class InstrumentSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    private val symbol = TextView(context)
    private val title = TextView(context)
    private val tuning = TextView(context)
    private var instrument: Instrument = Instrument.GUITAR

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        minimumHeight = context.dp(50f)
        setPadding(context.dp(8f), context.dp(6f), context.dp(8f), context.dp(6f))

        symbol.gravity = Gravity.CENTER
        symbol.includeFontPadding = false
        symbol.useMediumTypeface()
        symbol.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
        addView(symbol, LayoutParams(context.dp(28f), context.dp(28f)).apply {
            marginEnd = context.dp(8f)
        })

        val texts = LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }
        title.configureSingleLineText(
            R.dimen.struna_text_cents,
            R.color.struna_color_text_primary,
            medium = true,
        )
        tuning.configureSingleLineText(
            R.dimen.struna_text_scale,
            R.color.struna_color_text_secondary,
        )
        texts.addView(title, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        texts.addView(tuning, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(texts, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))

        isClickable = true
        isFocusable = true
    }

    fun bind(instrument: Instrument, selected: Boolean) {
        this.instrument = instrument
        when (instrument) {
            Instrument.GUITAR -> {
                symbol.text = "G"
                title.setText(R.string.selector_guitar)
                tuning.setText(R.string.tuning_guitar_notes)
            }
            Instrument.BASS -> {
                symbol.text = "B"
                title.setText(R.string.selector_bass)
                tuning.setText(R.string.tuning_bass_notes)
            }
        }

        if (selected) {
            background = context.roundedBackground(
                R.color.struna_color_accent_soft,
                12f,
                R.color.struna_color_accent,
            )
            symbol.background = context.roundedBackground(R.color.struna_color_accent, 8f)
            symbol.setTextColor(context.getColor(R.color.struna_color_surface))
            title.setTextColor(context.getColor(R.color.struna_color_accent))
            isSelected = true
        } else {
            background = context.roundedBackground(
                R.color.struna_color_surface,
                12f,
                R.color.struna_color_border,
            )
            symbol.background = context.roundedBackground(R.color.struna_color_accent_soft, 8f)
            symbol.setTextColor(context.getColor(R.color.struna_color_accent))
            title.setTextColor(context.getColor(R.color.struna_color_text_primary))
            isSelected = false
        }
        contentDescription = title.text
    }

    fun instrument(): Instrument = instrument
}
