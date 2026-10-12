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

/**
 * A deliberately simple, offline-first placeholder widget.
 * Keep rendering independent of network/Notion data so it never fails while
 * the Notion integration is being built.
 */
class LifeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(12.dp),
                verticalAlignment = Alignment.Vertical.Top,
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Text("NOTION LIFE")
                Spacer(GlanceModifier.height(8.dp))
                Text("Your daily dashboard")
                Spacer(GlanceModifier.height(12.dp))
                Text("Tasks  •  Goals  •  Habits")
                Spacer(GlanceModifier.height(6.dp))
                Text("Calendar: read-only")
                Spacer(GlanceModifier.height(12.dp))
                Text("Notion sync is not connected yet.")
            }
        }
    }
}

class LifeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LifeWidget()
}
