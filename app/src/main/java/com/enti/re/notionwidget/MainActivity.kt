package com.enti.re.notionwidget

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Gravity
import android.graphics.Color
import android.graphics.Typeface
import android.content.Context
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.glance.appwidget.GlanceAppWidgetManager

class MainActivity : Activity() {
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var content: LinearLayout
    private lateinit var status: TextView
    private lateinit var endpoint: EditText
    private lateinit var apiKey: EditText
    private val prefs by lazy { getSharedPreferences("life_dashboard", Context.MODE_PRIVATE) }
    private val defaultBackend = "https://notion-life-widget-api.onrender.com"
    private val defaultWidgetKey = ""
    private val ink = Color.rgb(38, 48, 43)
    private val muted = Color.rgb(105, 116, 109)
    private val paper = Color.rgb(248, 249, 246)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = paper
        window.navigationBarColor = paper
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        window.decorView.setBackgroundColor(paper)

        val scroll = ScrollView(this)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(28), dp(22), dp(28))
            setBackgroundColor(paper)
        }
        scroll.addView(content)
        setContentView(scroll)

        addText("NOTION LIFE", 12f, muted, true)
        addText("Your day, in one place.", 28f, ink, true, 12)
        addText("A quiet overview of your tasks, goals, habits and calendar.", 15f, muted, false, 22)
        status = addText("Not connected · showing your local dashboard", 13f, muted, true, 12)
        section("Today’s tasks")
        addText(prefs.getString("tasks_preview", "Connect your secure Notion backend to load tasks.")!!, 15f, ink, false, 4)
        section("Goals")
        addText(prefs.getString("goals_preview", "Your goals will appear here after syncing.")!!, 15f, ink, false, 4)
        section("Habits")
        addText(prefs.getString("habits_preview", "Your daily habits will appear here after syncing.")!!, 15f, ink, false, 4)
        section("Calendar · read-only")
        addText(prefs.getString("calendar_preview", "Upcoming events will appear here after syncing.")!!, 15f, ink, false, 4)

        section("Connect your backend")
        addText("The app never stores your Notion token. Enter the HTTPS URL of your backend, which should expose GET /dashboard and return JSON arrays named tasks, goals, habits and calendar.", 13f, muted, false, 8)
        endpoint = EditText(this).apply {
            hint = "https://your-server.example.com"
            setSingleLine(true)
            textSize = 14f
            setText(prefs.getString("backend_url", defaultBackend))
            setPadding(dp(12), dp(10), dp(12), dp(10))
        }
        content.addView(endpoint, LinearLayout.LayoutParams(-1, dp(52)))
        apiKey = EditText(this).apply {
            hint = "Widget API key"
            setSingleLine(true)
            textSize = 14f
            setText(prefs.getString("widget_api_key", defaultWidgetKey))
            setPadding(dp(12), dp(10), dp(12), dp(10))
        }
        content.addView(apiKey, LinearLayout.LayoutParams(-1, dp(52)))
        val sync = Button(this).apply {
            text = "Save backend & sync"
            isAllCaps = false
            setOnClickListener { syncDashboard() }
        }
        content.addView(sync, LinearLayout.LayoutParams(-1, dp(52)))
        addText("Backend contract: GET /dashboard → { tasks: [{title, completed}], goals: [{title, detail}], habits: [{title, completed}], calendar: [{title, date}] }. Keep the Notion integration secret on the server.", 12f, muted, false, 12)
        if (prefs.getString("backend_url", defaultBackend).orEmpty().isNotBlank()) status.text = "Backend configured · tap sync to refresh"
    }

    private fun syncDashboard() {
        val base = endpoint.text.toString().trim().trimEnd('/')
        if (!base.startsWith("https://")) {
            status.text = "Please enter a valid HTTPS backend URL."
            return
        }
        prefs.edit().putString("backend_url", base).putString("widget_api_key", apiKey.text.toString().trim()).apply()
        status.text = "Syncing securely…"
        executor.execute {
            var connection: HttpURLConnection? = null
            try {
                connection = (URL("$base/dashboard").openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 15000
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("X-Widget-Key", apiKey.text.toString().trim())
                }
                if (connection.responseCode !in 200..299) throw Exception("Server returned HTTP ${connection.responseCode}")
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(body)
                val tasks = formatItems(json.optJSONArray("tasks"), "title", "completed")
                val goals = formatItems(json.optJSONArray("goals"), "title", "detail")
                val habits = formatItems(json.optJSONArray("habits"), "title", "completed")
                val calendar = formatItems(json.optJSONArray("calendar"), "title", "date")
                prefs.edit()
                    .putString("tasks_preview", tasks.ifBlank { "No tasks returned." })
                    .putString("goals_preview", goals.ifBlank { "No goals returned." })
                    .putString("habits_preview", habits.ifBlank { "No habits returned." })
                    .putString("calendar_preview", calendar.ifBlank { "No upcoming events." })
                    .putString("synced_at", System.currentTimeMillis().toString())
                    .apply()
                runOnUiThread {
                    status.text = "Synced successfully"
                    recreate()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val widget = LifeWidget()
                    val manager = GlanceAppWidgetManager(this@MainActivity)
                    manager.getGlanceIds(LifeWidget::class.java).forEach { id ->
                        widget.update(this@MainActivity, id)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread { status.text = "Sync failed: ${e.message ?: "check backend URL and /dashboard response"}" }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun formatItems(array: JSONArray?, primary: String, secondary: String): String {
        if (array == null) return ""
        val lines = mutableListOf<String>()
        for (i in 0 until minOf(array.length(), 8)) {
            val item = array.optJSONObject(i) ?: continue
            val title = item.optString(primary, "").trim()
            if (title.isBlank()) continue
            val extra = item.opt(secondary)
            val prefix = if (secondary == "completed" && extra == true) "✓ " else "• "
            val suffix = if (extra != null && extra != JSONObject.NULL && secondary != "completed" && extra.toString().isNotBlank()) " · ${extra}" else ""
            lines.add("$prefix$title$suffix")
        }
        return lines.joinToString("\n")
    }

    private fun section(label: String) {
        addText(label, 18f, ink, true, 18)
    }

    private fun addText(text: String, size: Float, color: Int, bold: Boolean, top: Int = 0): TextView {
        val view = TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(top), 0, dp(6))
            gravity = Gravity.START
        }
        content.addView(view, LinearLayout.LayoutParams(-1, -2))
        return view
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
