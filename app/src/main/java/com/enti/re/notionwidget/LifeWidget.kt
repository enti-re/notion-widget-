package com.enti.re.notionwidget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.compose.ui.unit.dp

/** Displays the last successfully synced dashboard snapshot, even when offline. */
class LifeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("life_dashboard", Context.MODE_PRIVATE)
        val tasks = prefs.getString("tasks_preview", "Open Notion Life to connect your backend.") ?: ""
        val goals = prefs.getString("goals_preview", "Goals appear after first sync.") ?: ""
        val habits = prefs.getString("habits_preview", "Habits appear after first sync.") ?: ""
        val syncedAt = prefs.getString("synced_at", null)
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(12.dp),
                verticalAlignment = Alignment.Vertical.Top,
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Text("NOTION LIFE")
                Spacer(GlanceModifier.height(6.dp))
                Text("Today's tasks")
                Text(tasks.take(220))
                Spacer(GlanceModifier.height(8.dp))
                Text("Goals")
                Text(goals.take(100))
                Spacer(GlanceModifier.height(8.dp))
                Text("Habits")
                Text(habits.take(100))
                Spacer(GlanceModifier.height(8.dp))
                Text(if (syncedAt == null) "Not synced yet · open app to connect" else "Showing last synced snapshot")
            }
        }
    }
}

class LifeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LifeWidget()
}
