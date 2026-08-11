package com.stepbeatgo.app.ui.screens.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.stepbeatgo.app.ui.components.SectionCard
import com.stepbeatgo.app.ui.viewmodel.CalculationViewModel
import com.stepbeatgo.app.ui.viewmodel.NamedPlaylistTrack
import com.stepbeatgo.app.util.TempoSearchLauncher

@Composable
fun PlaylistScreen(
    calcVm: CalculationViewModel,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    var trackName by remember { mutableStateOf("") }
    var trackBpm by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Плейлист", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = calcVm.playlistName,
            onValueChange = { calcVm.playlistName = it },
            label = { Text("Название плейлиста") },
            modifier = Modifier.fillMaxWidth()
        )

        SectionCard {
            OutlinedTextField(
                value = trackName,
                onValueChange = { trackName = it },
                label = { Text("Название трека и исполнитель") },
                modifier = Modifier.fillMaxWidth()
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                OutlinedTextField(
                    value = trackBpm,
                    onValueChange = { trackBpm = it.filter { c -> c.isDigit() } },
                    label = { Text("Темп, BPM") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (trackName.isNotBlank()) TempoSearchLauncher.search(context, trackName)
                }) {
                    Icon(Icons.Filled.Search, contentDescription = "Узнать темп")
                }
            }
            Text(
                "Откроет поиск в браузере по этому треку — прочитай число и впиши его",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))
            Button(
                onClick = {
                    val bpm = trackBpm.toIntOrNull()
                    if (trackName.isNotBlank() && bpm != null && bpm > 0) {
                        calcVm.addTrack(NamedPlaylistTrack(name = trackName, bpm = bpm))
                        trackName = ""
                        trackBpm = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить трек")
            }
        }

        if (calcVm.playlistTracks.isEmpty()) {
            Text(
                "Пока нет треков — добавь первый",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(calcVm.playlistTracks.size) { index ->
                    val track = calcVm.playlistTracks[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(track.name, style = MaterialTheme.typography.bodyLarge)
                            Text("${track.bpm} BPM", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { calcVm.removeTrack(index) }) {
                            Icon(Icons.Filled.Close, contentDescription = "Удалить трек")
                        }
                    }
                }
            }
        }

        Button(
            onClick = onContinue,
            enabled = calcVm.playlistTracks.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Продолжить")
        }
    }
}
