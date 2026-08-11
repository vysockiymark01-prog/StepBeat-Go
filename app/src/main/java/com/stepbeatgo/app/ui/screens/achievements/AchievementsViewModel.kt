package com.stepbeatgo.app.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepbeatgo.app.data.db.AppDatabase
import com.stepbeatgo.app.data.model.GenreCatalog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class Achievement(val title: String, val unlocked: Boolean)

class AchievementsViewModel(database: AppDatabase) : ViewModel() {
    val achievements: StateFlow<List<Achievement>> = database.historyDao().observeAll()
        .map { entries ->
            val genreLabelsTried = entries.filter { it.mode == "genre" }.map { it.soundtrackLabel }.toSet()
            listOf(
                Achievement("Первые шаги — рассчитан первый маршрут", entries.isNotEmpty()),
                Achievement("Разогнался — рассчитано 5 маршрутов", entries.size >= 5),
                Achievement(
                    "Исследователь жанров — опробованы все встроенные жанры",
                    genreLabelsTried.size >= GenreCatalog.all.size
                ),
                Achievement("Куратор — собран первый плейлист", entries.any { it.mode == "playlist" })
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
