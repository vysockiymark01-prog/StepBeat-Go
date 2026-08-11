package com.stepbeatgo.app.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.stepbeatgo.app.calc.TempoCalculator
import com.stepbeatgo.app.ui.components.SectionCard
import com.stepbeatgo.app.util.ClipboardHelper
import com.stepbeatgo.app.util.MapsAppLauncher
import com.stepbeatgo.app.util.RouteLinkParser

private enum class InputMode { LINK, MANUAL }

@Composable
fun HomeScreen(
    onContinue: (routeName: String, baselineSeconds: Long, distanceKm: Double?) -> Unit
) {
    val context = LocalContext.current

    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var inputMode by remember { mutableStateOf(InputMode.LINK) }

    var linkText by remember { mutableStateOf("") }
    var parseMessage by remember { mutableStateOf<String?>(null) }
    var parseSucceeded by remember { mutableStateOf(false) }

    var minutesText by remember { mutableStateOf("") }
    var distanceText by remember { mutableStateOf("") }

    var showMapsDialog by remember { mutableStateOf(false) }

    fun tryParse(text: String) {
        linkText = text
        val parsed = RouteLinkParser.parse(text)
        if (parsed != null) {
            parseSucceeded = true
            parseMessage = "ok"
            val km = parsed.straightLineKm
            distanceText = String.format("%.1f", km)
            val estSeconds = TempoCalculator.baselineSecondsFromDistance(km)
            minutesText = ((estSeconds + 30) / 60).toString()
        } else {
            parseSucceeded = false
            parseMessage = if (text.isBlank()) null else "fail"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "StepBeat Go", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Сколько бы занял этот путь под нужный темп?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SectionCard {
            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("Откуда") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Куда") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showMapsDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Map, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("  Открыть в Картах")
            }
        }

        SectionCard {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SegmentButton(
                    text = "Вставить ссылку",
                    selected = inputMode == InputMode.LINK,
                    onClick = { inputMode = InputMode.LINK },
                    modifier = Modifier.weight(1f)
                )
                SegmentButton(
                    text = "Вписать время",
                    selected = inputMode == InputMode.MANUAL,
                    onClick = { inputMode = InputMode.MANUAL },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            AnimatedContent(targetState = inputMode, label = "input_mode") { mode ->
                when (mode) {
                    InputMode.LINK -> Column {
                        OutlinedTextField(
                            value = linkText,
                            onValueChange = { tryParse(it) },
                            label = { Text("Ссылка на маршрут") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            ClipboardHelper.readText(context)?.let { tryParse(it) }
                        }) {
                            Text("Вставить из буфера обмена")
                        }
                        when (parseMessage) {
                            "ok" -> Text(
                                "Ссылка на маршрут распознана",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            "fail" -> Text(
                                "Не удалось прочитать координаты — впиши время вручную ниже",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = minutesText,
                            onValueChange = { minutesText = it.filter { c -> c.isDigit() } },
                            label = { Text("Время пешком, минут (можно поправить)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    InputMode.MANUAL -> Column {
                        OutlinedTextField(
                            value = minutesText,
                            onValueChange = { minutesText = it.filter { c -> c.isDigit() } },
                            label = { Text("Время пешком, показанное Картами, минут") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = distanceText,
                onValueChange = { distanceText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Расстояние, км (необязательно)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        val minutes = minutesText.toIntOrNull()
        Button(
            onClick = {
                val label = when {
                    origin.isNotBlank() && destination.isNotBlank() -> "$origin → $destination"
                    origin.isNotBlank() -> origin
                    else -> "Маршрут"
                }
                val baseline = (minutes ?: 0) * 60L
                val distance = distanceText.toDoubleOrNull()
                onContinue(label, baseline, distance)
            },
            enabled = minutes != null && minutes > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Продолжить")
        }
    }

    if (showMapsDialog) {
        val providers = remember { MapsAppLauncher.installedProviders(context) }
        AlertDialog(
            onDismissRequest = { showMapsDialog = false },
            confirmButton = {},
            title = { Text("Выбери приложение карт") },
            text = {
                Column {
                    if (providers.isEmpty()) {
                        Text("Не найдено установленных карт — откроется браузер.")
                    }
                    val toShow = providers.ifEmpty { com.stepbeatgo.app.util.MapsProvider.entries }
                    toShow.forEach { provider ->
                        TextButton(onClick = {
                            showMapsDialog = false
                            MapsAppLauncher.openRoute(context, provider, origin.ifBlank { "Моё местоположение" }, destination.ifBlank { "" })
                        }) {
                            Text(provider.displayName)
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun SegmentButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (selected) {
        Button(onClick = onClick, modifier = modifier) { Text(text) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(text) }
    }
}
