package com.stepbeatgo.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepbeatgo.app.data.db.AppDatabase
import com.stepbeatgo.app.data.db.HistoryEntryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val database: AppDatabase) : ViewModel() {
    val entries: StateFlow<List<HistoryEntryEntity>> = database.historyDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clear() {
        viewModelScope.launch { database.historyDao().clear() }
    }
}
