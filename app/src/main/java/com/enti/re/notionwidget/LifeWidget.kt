package com.enti.re.notionwidget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
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
import androidx.glance.color.Color
import androidx.glance.Button
import androidx.glance.layout.fillMaxWidth
import androidx.glance.unit.dp
import androidx.glance.appwidget.action.actionStartActivity

class LifeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: android.content.Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFFF7F4ED)))
                .padding(16.dp),
            verticalAlignment = Alignment.Vertical.Top,
            horizontalAlignment = Alignment.Horizontal.Start
        ) {
            Text("TODAY, WITH INTENTION", style = TextStyle(color = ColorProvider(Color(0xFF69665E))))
            Spacer(GlanceModifier.height(6.dp))
            Text("Your life, in one glance", style = TextStyle(color = ColorProvider(Color(0xFF25241F))))
            Spacer(GlanceModifier.height(12.dp))
            Text("☐  Tasks from Notion")
            Text("○  Goals in progress")
            Text("□  Habits to check off")
            Text("▦  Calendar (read-only)")
            Spacer(GlanceModifier.height(10.dp))
            androidx.glance.Button(
                text = "Open dashboard",
                onClick = actionStartActivity<MainActivity>()
            )
        }
    }
}

class LifeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LifeWidget()
}
