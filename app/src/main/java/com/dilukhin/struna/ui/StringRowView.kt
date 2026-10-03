package com.dilukhin.struna.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.dilukhin.struna.R
import com.dilukhin.struna.domain.TuningString

class StringRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    private val number = TextView(context)
    private val note = TextView(context)
    private val octave = TextView(context)
    private val frequency = TextView(context)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        minimumHeight = context.dp(48f)
        setPadding(context.dp(14f), context.dp(8f), context.dp(14f), context.dp(8f))

        number.configureSingleLineText(
            R.dimen.struna_text_string_number,
            R.color.struna_color_text_secondary,
            gravityValue = Gravity.CENTER,
        )
        addView(number, LayoutParams(context.dp(18f), context.dp(18f)).apply {
            marginEnd = context.dp(12f)
        })

        note.configureSingleLineText(
            R.dimen.struna_text_string_note,
            R.color.struna_color_text_primary,
            medium = true,
        )
        addView(note, LayoutParams(context.dp(30f), context.dp(24f)).apply {
            marginEnd = context.dp(12f)
        })

        octave.configureSingleLineText(
            R.dimen.struna_text_string_number,
            R.color.struna_color_text_secondary,
            gravityValue = Gravity.CENTER,
        )
        addView(octave, LayoutParams(context.dp(18f), context.dp(18f)).apply {
            marginEnd = context.dp(12f)
        })

        addView(android.view.View(context), LayoutParams(0, 1, 1f).apply {
            marginEnd = context.dp(12f)
        })

        frequency.configureSingleLineText(
            R.dimen.struna_text_string_frequency,
            R.color.struna_color_text_secondary,
            gravityValue = Gravity.CENTER_VERTICAL or Gravity.END,
        )
        addView(frequency, LayoutParams(context.dp(82f), context.dp(20f)))
    }

    fun bind(spec: TuningString, active: Boolean) {
        number.text = spec.stringNumber.toString()
        note.text = spec.noteName
        octave.text = spec.octave.toString()
        frequency.text = context.getString(R.string.frequency_format, spec.frequencyHz())
        if (active) {
            background = context.roundedBackground(
                R.color.struna_color_accent_soft,
                16f,
                R.color.struna_color_accent,
                strokeWidthDp = 1.5f,
            )
            note.setTextColor(context.getColor(R.color.struna_color_accent))
        } else {
            background = context.roundedBackground(
                R.color.struna_color_surface,
                16f,
                R.color.struna_color_border,
            )
            note.setTextColor(context.getColor(R.color.struna_color_text_primary))
        }
        contentDescription = spec.stringNumber.toString() + " " + spec.displayNote
    }
}
