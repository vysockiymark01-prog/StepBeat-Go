package com.stepbeatgo.app.ui.screens.genre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stepbeatgo.app.data.model.Genre
import com.stepbeatgo.app.data.model.GenreCatalog
import com.stepbeatgo.app.ui.components.Pill

@Composable
fun GenreScreen(
    onGenreChosen: (Genre) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(20.dp)) {
        Text("Жанр", style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(GenreCatalog.all) { genre ->
                Card(
                    onClick = { onGenreChosen(genre) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(genre.nameRes, style = MaterialTheme.typography.bodyLarge)
                        Pill(text = "${genre.bpm} BPM")
                    }
                }
            }
        }
    }
}
