package com.stepbeatgo.app.ui.screens.metronome

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.background
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.stepbeatgo.app.util.MetronomeEngine
import kotlinx.coroutines.launch

@Composable
fun MetronomeScreen(bpm: Int) {
    val engine = remember { MetronomeEngine() }
    val scope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    val pulse = remember { Animatable(1f) }

    DisposableEffect(Unit) {
        onDispose { engine.stop() }
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text("Клик-трек", style = MaterialTheme.typography.headlineMedium)
        Text("$bpm BPM", style = MaterialTheme.typography.displayLarge)

        androidx.compose.foundation.layout.Spacer(Modifier.padding(24.dp))
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer(scaleX = pulse.value, scaleY = pulse.value)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        androidx.compose.foundation.layout.Spacer(Modifier.padding(24.dp))

        Button(onClick = {
            if (isPlaying) {
                engine.stop()
                isPlaying = false
            } else {
                engine.start(scope, bpm) {
                    scope.launch {
                        pulse.snapTo(1.3f)
                        pulse.animateTo(1f, tween(150))
                    }
                }
                isPlaying = true
            }
        }) {
            Text(if (isPlaying) "Остановить" else "Начать")
        }
    }
}
