package com.stepbeatgo.app.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepbeatgo.app.data.db.AppDatabase
import com.stepbeatgo.app.data.db.FavoriteRouteEntity
import com.stepbeatgo.app.data.db.HistoryEntryEntity
import kotlinx.coroutines.launch

class ResultViewModel(private val database: AppDatabase) : ViewModel() {

    fun recordHistory(routeName: String, mode: String, soundtrackLabel: String, baselineSeconds: Long, resultSeconds: Long) {
        viewModelScope.launch {
            database.historyDao().insert(
                HistoryEntryEntity(
                    routeName = routeName,
                    mode = mode,
                    soundtrackLabel = soundtrackLabel,
                    baselineSeconds = baselineSeconds,
                    resultSeconds = resultSeconds
                )
            )
        }
    }

    fun saveFavorite(name: String, baselineSeconds: Long, distanceKm: Double?, onSaved: () -> Unit) {
        viewModelScope.launch {
            database.favoriteRouteDao().insert(
                FavoriteRouteEntity(
                    name = name,
                    baselineSeconds = baselineSeconds,
                    distanceKm = distanceKm
                )
            )
            onSaved()
        }
    }
}
