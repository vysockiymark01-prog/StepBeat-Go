package com.stepbeatgo.app.util

import android.content.ClipboardManager
import android.content.Context

object ClipboardHelper {
    fun readText(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(context)?.toString()
    }
}
