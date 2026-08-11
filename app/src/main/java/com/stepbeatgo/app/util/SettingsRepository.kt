package com.stepbeatgo.app.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.stepbeatgo.app.calc.DEFAULT_BASE_BPM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "stepbeatgo_settings")

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class DistanceUnit { KM, MI }

/** All user-configurable settings, persisted locally via DataStore. Nothing
 * here is ever sent anywhere — it's read/written only on this device. */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
        val BASE_PACE_BPM = intPreferencesKey("base_pace_bpm")
        val LANGUAGE = stringPreferencesKey("language") // "system", "en", "ru"
        val PREFERRED_MAPS_PACKAGE = stringPreferencesKey("preferred_maps_package")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    val distanceUnit: Flow<DistanceUnit> = context.dataStore.data.map { prefs ->
        prefs[Keys.DISTANCE_UNIT]?.let { runCatching { DistanceUnit.valueOf(it) }.getOrNull() } ?: DistanceUnit.KM
    }

    suspend fun setDistanceUnit(unit: DistanceUnit) {
        context.dataStore.edit { it[Keys.DISTANCE_UNIT] = unit.name }
    }

    val basePaceBpm: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.BASE_PACE_BPM] ?: DEFAULT_BASE_BPM
    }

    suspend fun setBasePaceBpm(bpm: Int) {
        context.dataStore.edit { it[Keys.BASE_PACE_BPM] = bpm }
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE] ?: "system"
    }

    suspend fun setLanguage(languageTag: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = languageTag }
    }

    val preferredMapsPackage: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.PREFERRED_MAPS_PACKAGE]
    }

    suspend fun setPreferredMapsPackage(packageName: String) {
        context.dataStore.edit { it[Keys.PREFERRED_MAPS_PACKAGE] = packageName }
    }
}
