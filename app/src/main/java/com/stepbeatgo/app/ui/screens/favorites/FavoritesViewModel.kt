package com.stepbeatgo.app.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepbeatgo.app.data.db.AppDatabase
import com.stepbeatgo.app.data.db.FavoriteRouteEntity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val database: AppDatabase) : ViewModel() {
    val favorites: StateFlow<List<FavoriteRouteEntity>> = database.favoriteRouteDao().observeAll()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(route: FavoriteRouteEntity) {
        viewModelScope.launch { database.favoriteRouteDao().delete(route) }
    }
}
