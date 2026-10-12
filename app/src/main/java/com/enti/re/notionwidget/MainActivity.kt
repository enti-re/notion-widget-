package com.enti.re.notionwidget

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

// Main entry point for the Notion widget companion app.
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val padding = (24 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(padding, padding, padding, padding)
        }

        root.addView(TextView(this).apply {
            text = "Notion Life Widget"
            textSize = 26f
        })
        root.addView(TextView(this).apply {
            text = "Your calm daily dashboard is taking shape."
            textSize = 16f
            setPadding(0, padding / 3, 0, padding / 2)
        })
        root.addView(TextView(this).apply {
            text = "Next: connect your Notion tasks, goals, habits, and read-only calendar.\n\nNotion credentials will stay on a secure backend, not in this app."
            textSize = 14f
        })

        setContentView(root)
    }
}
