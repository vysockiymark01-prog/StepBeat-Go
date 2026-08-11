package com.stepbeatgo.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stepbeatgo.app.StepBeatApp
import com.stepbeatgo.app.calc.DEFAULT_BASE_BPM
import com.stepbeatgo.app.ui.components.SectionCard
import com.stepbeatgo.app.util.DistanceUnit
import com.stepbeatgo.app.util.LocaleHelper
import com.stepbeatgo.app.util.ThemeMode

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as StepBeatApp
    val vm: SettingsViewModel = viewModel(factory = viewModelFactory {
        initializer { SettingsViewModel(app.settings) }
    })

    val theme by vm.themeMode.collectAsState()
    val unit by vm.distanceUnit.collectAsState()
    val pace by vm.basePaceBpm.collectAsState()
    val language by vm.language.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)

        SectionCard {
            Text("Оформление", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceChip("Светлая", theme == ThemeMode.LIGHT) { vm.setThemeMode(ThemeMode.LIGHT) }
                ChoiceChip("Тёмная", theme == ThemeMode.DARK) { vm.setThemeMode(ThemeMode.DARK) }
                ChoiceChip("Системная", theme == ThemeMode.SYSTEM) { vm.setThemeMode(ThemeMode.SYSTEM) }
            }
        }

        SectionCard {
            Text("Единицы измерения", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceChip("Километры", unit == DistanceUnit.KM) { vm.setDistanceUnit(DistanceUnit.KM) }
                ChoiceChip("Мили", unit == DistanceUnit.MI) { vm.setDistanceUnit(DistanceUnit.MI) }
            }
        }

        SectionCard {
            Text("Язык", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceChip("Системный", language == "system") { vm.setLanguage("system"); LocaleHelper.apply("system") }
                ChoiceChip("RU", language == "ru") { vm.setLanguage("ru"); LocaleHelper.apply("ru") }
                ChoiceChip("EN", language == "en") { vm.setLanguage("en"); LocaleHelper.apply("en") }
            }
        }

        SectionCard {
            Text("Твой обычный темп ходьбы", style = MaterialTheme.typography.titleLarge)
            Text(
                "Используется как базовый темп — тот, что закладывают карты для обычной прогулки",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))
            Text("$pace шагов / мин", style = MaterialTheme.typography.headlineMedium)
            TapTempoCalibrator(onMeasured = { vm.setBasePaceBpm(it) })
            OutlinedButton(onClick = { vm.setBasePaceBpm(DEFAULT_BASE_BPM) }) {
                Text("Сбросить на значение по умолчанию ($DEFAULT_BASE_BPM)")
            }
        }

        SectionCard {
            Text("О приложении", style = MaterialTheme.typography.titleLarge)
            Text(
                "StepBeat Go не делает собственных сетевых запросов. Открытие маршрутов и поиск темпа происходят прямо в твоём браузере или картах — мы ничего не получаем.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ChoiceChip(text: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick) { Text(text) }
    } else {
        OutlinedButton(onClick = onClick) { Text(text) }
    }
}

/**
 * Lets the user find their real walking cadence by tapping in rhythm with
 * their own steps for a few taps — the average interval between taps gives
 * the BPM directly, no stopwatch math required from the user.
 */
@Composable
private fun TapTempoCalibrator(onMeasured: (Int) -> Unit) {
    var tapTimestamps by remember { mutableStateOf<List<Long>>(emptyList()) }
    var measuredBpm by remember { mutableStateOf<Int?>(null) }

    Column {
        Button(onClick = {
            val now = System.currentTimeMillis()
            val updated = (tapTimestamps + now).takeLast(8)
            tapTimestamps = updated
            if (updated.size >= 4) {
                val intervals = updated.zipWithNext { a, b -> b - a }
                val avgMs = intervals.average()
                if (avgMs > 0) {
                    measuredBpm = (60000.0 / avgMs).toInt().coerceIn(40, 220)
                }
            }
        }) {
            Text("Шагай в такт и жми тут (мин. 4 раза)")
        }
        measuredBpm?.let { bpm ->
            Text("Измерено: $bpm BPM")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    onMeasured(bpm)
                    tapTimestamps = emptyList()
                    measuredBpm = null
                }) { Text("Применить") }
                OutlinedButton(onClick = {
                    tapTimestamps = emptyList()
                    measuredBpm = null
                }) { Text("Заново") }
            }
        }
    }
}
