package com.dilukhin.struna

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.dilukhin.struna.ui.TunerUiFixtures
import com.dilukhin.struna.ui.TunerView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tunerView = findViewById<TunerView>(R.id.tuner_view)
        val isDebuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        val fixture = if (isDebuggable) {
            TunerUiFixtures.fromKey(intent.getStringExtra(EXTRA_UI_FIXTURE))
        } else {
            null
        }

        tunerView.render(fixture ?: TunerUiFixtures.waiting())
    }

    companion object {
        const val EXTRA_UI_FIXTURE = "struna.ui.fixture"
    }
}
