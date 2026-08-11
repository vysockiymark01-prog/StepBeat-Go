package com.stepbeatgo.app.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Opens a web search for a track's tempo in an in-app Custom Tab. The app
 * never reads or scrapes the result — the user reads the number themselves
 * and types it into the track's BPM field. No API, no scraping, no network
 * call made by StepBeat Go itself.
 */
object TempoSearchLauncher {

    fun search(context: Context, trackQuery: String) {
        val query = Uri.encode("$trackQuery bpm tempo")
        val uri = Uri.parse("https://www.google.com/search?q=$query")
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, uri)
    }
}
