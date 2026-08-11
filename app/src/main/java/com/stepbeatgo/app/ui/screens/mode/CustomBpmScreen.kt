package com.stepbeatgo.app.ui.screens.mode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomBpmScreen(
    initialBpm: Int,
    onConfirm: (Int) -> Unit
) {
    var bpm by remember { mutableFloatStateOf(initialBpm.toFloat()) }

    Column(Modifier.fillMaxWidth().padding(20.dp)) {
        Text("Свой BPM", style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Spacer(Modifier.padding(12.dp))
        Text("${bpm.toInt()} BPM", style = MaterialTheme.typography.displayLarge)
        Slider(
            value = bpm,
            onValueChange = { bpm = it },
            valueRange = 40f..220f
        )
        androidx.compose.foundation.layout.Spacer(Modifier.padding(12.dp))
        Button(onClick = { onConfirm(bpm.toInt()) }, modifier = Modifier.fillMaxWidth()) {
            Text("Продолжить")
        }
    }
}
