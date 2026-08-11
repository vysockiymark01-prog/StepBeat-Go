package com.stepbeatgo.app.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stepbeatgo.app.StepBeatApp
import com.stepbeatgo.app.ui.components.SectionCard

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as StepBeatApp
    val vm: HistoryViewModel = viewModel(factory = viewModelFactory {
        initializer { HistoryViewModel(app.database) }
    })
    val entries by vm.entries.collectAsState()

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("История", style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))

        if (entries.isEmpty()) {
            Text(
                "Здесь появятся твои прошлые расчёты",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            TextButton(onClick = { vm.clear() }) { Text("Очистить историю") }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(entries, key = { it.id }) { entry ->
                    SectionCard(modifier = Modifier.fillMaxWidth()) {
                        Text(entry.routeName, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${entry.soundtrackLabel} · ${entry.resultSeconds / 60} мин",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
