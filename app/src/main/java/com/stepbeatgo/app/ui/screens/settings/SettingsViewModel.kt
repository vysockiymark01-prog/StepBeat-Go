package com.stepbeatgo.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepbeatgo.app.util.DistanceUnit
import com.stepbeatgo.app.util.SettingsRepository
import com.stepbeatgo.app.util.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settings: SettingsRepository) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settings.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val distanceUnit: StateFlow<DistanceUnit> = settings.distanceUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DistanceUnit.KM)

    val basePaceBpm: StateFlow<Int> = settings.basePaceBpm
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.stepbeatgo.app.calc.DEFAULT_BASE_BPM)

    val language: StateFlow<String> = settings.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { settings.setThemeMode(mode) }
    fun setDistanceUnit(unit: DistanceUnit) = viewModelScope.launch { settings.setDistanceUnit(unit) }
    fun setBasePaceBpm(bpm: Int) = viewModelScope.launch { settings.setBasePaceBpm(bpm) }
    fun setLanguage(tag: String) = viewModelScope.launch { settings.setLanguage(tag) }
}
