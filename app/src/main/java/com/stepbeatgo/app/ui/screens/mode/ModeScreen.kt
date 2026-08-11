package com.stepbeatgo.app.ui.screens.mode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stepbeatgo.app.ui.viewmodel.SoundtrackMode

@Composable
fun ModeScreen(
    onModeChosen: (SoundtrackMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Выбери саундтрек", style = MaterialTheme.typography.headlineMedium)

        ModeCard(
            title = "Жанр",
            description = "Выбери стиль музыки — используем его средний темп",
            onClick = { onModeChosen(SoundtrackMode.GENRE) }
        )
        ModeCard(
            title = "Мой плейлист",
            description = "Впиши названия треков и их темп сам",
            onClick = { onModeChosen(SoundtrackMode.PLAYLIST) }
        )
        ModeCard(
            title = "Свой BPM",
            description = "Введи точный темп вручную",
            onClick = { onModeChosen(SoundtrackMode.CUSTOM_BPM) }
        )
    }
}

@Composable
private fun ModeCard(title: String, description: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
