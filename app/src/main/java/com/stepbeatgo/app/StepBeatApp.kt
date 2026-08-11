package com.stepbeatgo.app

import android.app.Application
import com.stepbeatgo.app.data.db.AppDatabase
import com.stepbeatgo.app.util.SettingsRepository

class StepBeatApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val settings: SettingsRepository by lazy { SettingsRepository(this) }
}
