package com.stepbeatgo.app.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stepbeatgo.app.StepBeatApp
import com.stepbeatgo.app.calc.TempoCalculator
import com.stepbeatgo.app.ui.components.SectionCard

@Composable
fun FavoritesScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as StepBeatApp
    val vm: FavoritesViewModel = viewModel(factory = viewModelFactory {
        initializer { FavoritesViewModel(app.database) }
    })
    val favorites by vm.favorites.collectAsState()

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Избранное", style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))

        if (favorites.isEmpty()) {
            Text(
                "Сохрани маршрут, чтобы найти его здесь позже",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(favorites, key = { it.id }) { fav ->
                    SectionCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(fav.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    TempoCalculator.formatDuration(fav.baselineSeconds),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { vm.delete(fav) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}
