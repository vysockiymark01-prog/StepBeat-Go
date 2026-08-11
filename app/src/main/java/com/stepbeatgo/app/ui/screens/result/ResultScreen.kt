package com.stepbeatgo.app.ui.screens.result

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.stepbeatgo.app.calc.TempoCalculator
import com.stepbeatgo.app.ui.components.AnimatedCounter
import com.stepbeatgo.app.ui.components.SectionCard
import com.stepbeatgo.app.ui.viewmodel.CalculationViewModel
import com.stepbeatgo.app.ui.viewmodel.SoundtrackMode

@Composable
fun ResultScreen(
    calcVm: CalculationViewModel,
    onOpenMetronome: (Int) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StepBeatApp
    val resultVm: ResultViewModel = viewModel(factory = viewModelFactory {
        initializer { ResultViewModel(app.database) }
    })

    val resultSeconds = remember { calcVm.resultSeconds() }
    val baselineSeconds = calcVm.baselineSeconds
    val label = remember { calcVm.soundtrackLabel() }
    val modeTag = when (calcVm.mode) {
        SoundtrackMode.GENRE -> "genre"
        SoundtrackMode.PLAYLIST -> "playlist"
        SoundtrackMode.CUSTOM_BPM -> "custom"
    }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        resultVm.recordHistory(
            routeName = calcVm.routeName,
            mode = modeTag,
            soundtrackLabel = label,
            baselineSeconds = baselineSeconds,
            resultSeconds = resultSeconds
        )
    }

    Column(
        Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Результат", style = MaterialTheme.typography.headlineMedium)

        SectionCard {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            AnimatedCounter(targetValue = (resultSeconds / 60).toInt())
            Text("минут под этот саундтрек", style = MaterialTheme.typography.bodyLarge)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))
            Text(
                "Обычным шагом: ${TempoCalculator.formatDuration(baselineSeconds)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            calcVm.distanceKm?.let {
                Text(
                    "Расстояние: %.1f км".format(it),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (calcVm.mode == SoundtrackMode.PLAYLIST) {
            val playlistResult = remember { calcVm.playlistResult() }
            SectionCard {
                Text("По трекам", style = MaterialTheme.typography.titleLarge)
                Text("Треков понадобится: ${playlistResult.tracksUsedTotal}")
            }
        } else {
            SectionCard {
                Text("Понадобится треков: ${calcVm.tracksNeededForGenreOrCustom()}")
            }
        }

        val bpmForMetronome = when (calcVm.mode) {
            SoundtrackMode.GENRE -> calcVm.selectedGenre?.bpm
            SoundtrackMode.CUSTOM_BPM -> calcVm.customBpm
            SoundtrackMode.PLAYLIST -> calcVm.playlistTracks.firstOrNull()?.bpm
        }

        bpmForMetronome?.let { bpm ->
            OutlinedButton(onClick = { onOpenMetronome(bpm) }, modifier = Modifier.fillMaxWidth()) {
                Text("Идти под клик-трек")
            }
        }

        Button(
            onClick = {
                resultVm.saveFavorite(calcVm.routeName, baselineSeconds, calcVm.distanceKm) {
                    saved = true
                }
            },
            enabled = !saved,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (saved) "Сохранено в избранное" else "Сохранить в избранное")
        }

        OutlinedButton(
            onClick = {
                val text = "${calcVm.routeName} — этот путь займёт ${resultSeconds / 60} мин под $label в StepBeat Go"
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Поделиться")
        }
    }
}
