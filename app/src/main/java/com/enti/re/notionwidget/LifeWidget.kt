package com.enti.re.notionwidget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

class LifeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: android.content.Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(Color(0xFFF7F4ED)))
                    .padding(16.dp),
                verticalAlignment = Alignment.Vertical.Top,
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Text("NOTION LIFE", style = TextStyle(color = ColorProvider(Color(0xFF69665E))))
                Spacer(GlanceModifier.height(8.dp))
                Text("Your daily dashboard", style = TextStyle(color = ColorProvider(Color(0xFF25241F))))
                Spacer(GlanceModifier.height(12.dp))
                Text("Tasks  ·  Goals  ·  Habits")
                Spacer(GlanceModifier.height(6.dp))
                Text("Calendar is read-only")
                Spacer(GlanceModifier.height(12.dp))
                Text("Notion sync is not connected yet.")
            }
        }
    }
}

class LifeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LifeWidget()
}
