package com.stepbeatgo.app.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** Per-app language override (Android 13+ system setting, backward-compatible
 * via AppCompat down to API 21). "system" clears the override and follows
 * the device language. */
object LocaleHelper {
    fun apply(languageTag: String) {
        val locales = if (languageTag == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
