package com.dilukhin.struna.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.dilukhin.struna.R

class MicrophonePermissionCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    val allowButton = ActionButtonView(context)
    val settingsButton = ActionButtonView(context)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(context.dp(24f), context.dp(30f), context.dp(24f), context.dp(30f))
        background = context.roundedBackground(
            R.color.struna_color_surface,
            24f,
            R.color.struna_color_border,
        )

        addView(createMicrophoneIcon(), LayoutParams(context.dp(64f), context.dp(64f)))

        val title = TextView(context).apply {
            configureSingleLineText(
                R.dimen.struna_text_title,
                R.color.struna_color_text_primary,
                medium = true,
                gravityValue = Gravity.CENTER,
            )
            setText(R.string.microphone_permission_title)
        }
        addView(title, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(32f)).apply {
            topMargin = context.dp(16f)
        })

        val body = TextView(context).apply {
            includeFontPadding = false
            gravity = Gravity.CENTER
            useRegularTypeface()
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15f)
            setTextColor(context.getColor(R.color.struna_color_text_secondary))
            setText(R.string.microphone_permission_body)
            maxLines = 4
        }
        addView(body, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(72f)).apply {
            topMargin = context.dp(16f)
        })

        val actions = LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        allowButton.configure(R.string.action_allow_microphone, ActionButtonStyle.PRIMARY)
        settingsButton.configure(R.string.action_open_settings, ActionButtonStyle.SECONDARY)
        actions.addView(allowButton, LayoutParams(context.dp(180f), context.dp(48f)))
        actions.addView(settingsButton, LayoutParams(context.dp(180f), context.dp(48f)).apply {
            topMargin = context.dp(10f)
        })
        addView(actions, LayoutParams(LayoutParams.MATCH_PARENT, context.dp(106f)).apply {
            topMargin = context.dp(16f)
        })
    }

    private fun createMicrophoneIcon(): FrameLayout = FrameLayout(context).apply {
        background = context.roundedBackground(R.color.struna_color_accent_soft, 20f)

        fun part(width: Float, height: Float, left: Float, top: Float, radius: Float) {
            addView(View(context).apply {
                background = context.roundedBackground(R.color.struna_color_accent, radius)
            }, FrameLayout.LayoutParams(context.dp(width), context.dp(height)).apply {
                leftMargin = context.dp(left)
                topMargin = context.dp(top)
            })
        }

        part(18f, 30f, 23f, 13f, 9f)
        part(3f, 10f, 30.5f, 40f, 2f)
        part(20f, 3f, 22f, 49f, 2f)
    }
}
