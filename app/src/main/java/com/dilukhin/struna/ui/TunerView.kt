package com.dilukhin.struna.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.dilukhin.struna.R
import com.dilukhin.struna.domain.Instrument
import com.dilukhin.struna.domain.TuningCatalog
import java.util.Locale
import kotlin.math.abs

class TunerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    private val guitarSelector = InstrumentSelectorView(context)
    private val bassSelector = InstrumentSelectorView(context)
    private val normalBody = LinearLayout(context)
    private val tunerPanel = TunerPanelView(context)
    private val stabilityCard = StabilityCardView(context)
    private val tuningLabel = TextView(context)
    private val stringList = LinearLayout(context)
    private val footer = TextView(context)
    private val permissionCard = MicrophonePermissionCardView(context)
    private var currentState = TunerUiFixtures.waiting()

    init {
        orientation = VERTICAL
        setPadding(
            context.dp(20f),
            context.dp(20f),
            context.dp(20f),
            context.dp(20f),
        )
        setBackgroundColor(context.getColor(R.color.struna_color_background))

        addView(createTopBar(), LayoutParams(LayoutParams.MATCH_PARENT, context.dp(48f)))
        addGap(10f)

        normalBody.orientation = VERTICAL
        normalBody.addView(createInstrumentSwitch(), LayoutParams(LayoutParams.MATCH_PARENT, context.dp(50f)))
        normalBody.addGap(10f)
        normalBody.addView(tunerPanel, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(215f)))
        normalBody.addGap(10f)
        normalBody.addView(stabilityCard, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(84f)))
        normalBody.addGap(10f)
        normalBody.addView(createStringsHeader(), LayoutParams(LayoutParams.MATCH_PARENT, context.dp(24f)))
        normalBody.addGap(10f)
        stringList.orientation = VERTICAL
        normalBody.addView(stringList, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(308f)))
        normalBody.addGap(10f)
        footer.configureSingleLineText(
            R.dimen.struna_text_caption,
            R.color.struna_color_text_secondary,
            gravityValue = Gravity.CENTER,
        )
        normalBody.addView(footer, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(20f)))
        addView(normalBody, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        permissionCard.visibility = GONE
        addView(permissionCard, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(380f)))

        guitarSelector.setOnClickListener {
            render(TunerUiFixtures.waiting(Instrument.GUITAR))
        }
        bassSelector.setOnClickListener {
            render(TunerUiFixtures.waiting(Instrument.BASS))
        }

        render(currentState)
    }

    fun render(state: TunerUiState) {
        currentState = state
        val permissionRequired =
            state.displayState == TuningDisplayState.MICROPHONE_PERMISSION_REQUIRED
        normalBody.visibility = if (permissionRequired) GONE else VISIBLE
        permissionCard.visibility = if (permissionRequired) VISIBLE else GONE
        if (permissionRequired) return

        guitarSelector.bind(Instrument.GUITAR, state.instrument == Instrument.GUITAR)
        bassSelector.bind(Instrument.BASS, state.instrument == Instrument.BASS)
        tunerPanel.render(state)
        stabilityCard.setQuality(state.signalQuality)

        tuningLabel.setText(
            if (state.instrument == Instrument.GUITAR) {
                R.string.tuning_standard
            } else {
                R.string.tuning_standard_bass
            },
        )

        rebuildStringRows(state)

        footer.setText(
            when {
                state.displayState == TuningDisplayState.WAITING -> R.string.footer_waiting
                state.instrument == Instrument.BASS -> R.string.footer_bass
                else -> R.string.footer_offline
            },
        )
    }

    private fun createTopBar(): LinearLayout = LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val menu = TextView(context).apply {
            includeFontPadding = false
            gravity = Gravity.CENTER
            useRegularTypeface()
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 24f)
            setTextColor(context.getColor(R.color.struna_color_text_primary))
            text = "☰"
            contentDescription = context.getString(R.string.menu_open_navigation)
        }
        addView(menu, LayoutParams(context.dp(48f), context.dp(48f)).apply {
            marginEnd = context.dp(10f)
        })

        val title = TextView(context).apply {
            configureSingleLineText(
                R.dimen.struna_text_title,
                R.color.struna_color_text_primary,
                medium = true,
            )
            setText(R.string.tuner_title)
        }
        addView(title, LayoutParams(0, context.dp(30f), 1f).apply {
            marginEnd = context.dp(10f)
        })

        addView(View(context), LayoutParams(context.dp(22f), 1).apply {
            marginEnd = context.dp(10f)
        })

        val status = StatusChipView(context)
        addView(status, LayoutParams(LayoutParams.WRAP_CONTENT, context.dp(30f)))
    }

    private fun createInstrumentSwitch(): LinearLayout = LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(context.dp(9f), 0, context.dp(9f), 0)
        addView(guitarSelector, LayoutParams(0, LayoutParams.MATCH_PARENT, 1f).apply {
            marginEnd = context.dp(8f)
        })
        addView(bassSelector, LayoutParams(0, LayoutParams.MATCH_PARENT, 1f))
    }

    private fun createStringsHeader(): LinearLayout = LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val title = TextView(context).apply {
            configureSingleLineText(
                R.dimen.struna_text_body,
                R.color.struna_color_text_primary,
                medium = true,
            )
            setText(R.string.strings_title)
        }
        addView(title, LayoutParams(0, LayoutParams.MATCH_PARENT, 1f))

        tuningLabel.configureSingleLineText(
            R.dimen.struna_text_caption,
            R.color.struna_color_text_secondary,
            gravityValue = Gravity.CENTER_VERTICAL or Gravity.END,
        )
        addView(tuningLabel, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
    }

    private fun rebuildStringRows(state: TunerUiState) {
        stringList.removeAllViews()
        val profile = TuningCatalog.profile(state.instrument)
        profile.strings.forEachIndexed { index, spec ->
            val row = StringRowView(context)
            row.bind(spec, active = spec.stringNumber == state.activeStringNumber)
            stringList.addView(row, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(48f)).apply {
                if (index > 0) topMargin = context.dp(4f)
            })
        }
    }
}

private class TunerPanelView(context: Context) : FrameLayout(context) {
    private val note = TextView(context)
    private val frequency = TextView(context)
    private val cents = TextView(context)
    private val gauge = CentsGaugeView(context)
    private val status = TextView(context)

    init {
        note.apply {
            includeFontPadding = false
            gravity = Gravity.CENTER
            useBoldTypeface()
            useTextSize(R.dimen.struna_text_note)
            setTextColor(context.getColor(R.color.struna_color_text_primary))
        }
        addView(note, centeredLayout(120f, 68f, -5f))

        frequency.configureSingleLineText(
            R.dimen.struna_text_body,
            R.color.struna_color_text_secondary,
            gravityValue = Gravity.CENTER,
        )
        addView(frequency, centeredLayout(120f, 22f, 68f))

        cents.configureSingleLineText(
            R.dimen.struna_text_cents,
            R.color.struna_color_text_primary,
            medium = true,
            gravityValue = Gravity.CENTER,
        )
        addView(cents, centeredLayout(140f, 20f, 95f))

        addView(gauge, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(73f)).apply {
            gravity = Gravity.TOP
            leftMargin = context.dp(26f)
            rightMargin = context.dp(26f)
            topMargin = context.dp(120f)
        })

        status.configureSingleLineText(
            R.dimen.struna_text_body,
            R.color.struna_color_accent,
            medium = true,
            gravityValue = Gravity.CENTER,
        )
        addView(status, centeredLayout(180f, 22f, 198f))
    }

    fun render(state: TunerUiState) {
        if (state.displayState == TuningDisplayState.WAITING) {
            note.text = "—"
            frequency.setText(R.string.listening)
            cents.setText(R.string.play_string)
            status.setText(R.string.state_waiting)
            status.setTextColor(context.getColor(R.color.struna_color_text_secondary))
            gauge.setReading(null, state.displayState)
            return
        }

        note.text = state.note ?: "—"
        frequency.text = state.frequencyHz?.let {
            context.getString(R.string.frequency_format, it)
        } ?: "—"
        cents.text = state.cents?.let(::formatCents) ?: "—"

        val statusRes = when (state.displayState) {
            TuningDisplayState.FLAT -> R.string.state_flat
            TuningDisplayState.IN_TUNE -> R.string.state_in_tune
            TuningDisplayState.SHARP -> R.string.state_sharp
            TuningDisplayState.UNSTABLE ->
                if ((state.cents ?: 0.0) <= 0.0) R.string.state_flat else R.string.state_sharp
            TuningDisplayState.WAITING -> R.string.state_waiting
            TuningDisplayState.MICROPHONE_PERMISSION_REQUIRED ->
                R.string.state_microphone_permission_required
        }
        status.setText(statusRes)
        status.setTextColor(
            context.getColor(
                when (state.displayState) {
                    TuningDisplayState.FLAT -> R.color.struna_color_low
                    TuningDisplayState.SHARP -> R.color.struna_color_high
                    TuningDisplayState.UNSTABLE ->
                        if ((state.cents ?: 0.0) <= 0.0) {
                            R.color.struna_color_low
                        } else {
                            R.color.struna_color_high
                        }
                    TuningDisplayState.WAITING ->
                        R.color.struna_color_text_secondary
                    else -> R.color.struna_color_accent
                },
            ),
        )
        gauge.setReading(state.cents, state.displayState)
    }

    private fun formatCents(value: Double): String {
        if (abs(value) < 0.5) return context.getString(R.string.cents_zero)
        val signed = String.format(Locale.getDefault(), "%+.0f", value).replace('-', '−')
        return context.getString(R.string.cents_value_format, signed)
    }

    private fun centeredLayout(widthDp: Float, heightDp: Float, topDp: Float) =
        LayoutParams(context.dp(widthDp), context.dp(heightDp)).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            topMargin = context.dp(topDp)
        }
}
