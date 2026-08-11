package com.stepbeatgo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.stepbeatgo.app.ui.navigation.StepBeatNavGraph
import com.stepbeatgo.app.ui.theme.StepBeatGoTheme
import com.stepbeatgo.app.util.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as StepBeatApp

        setContent {
            val themeMode by app.settings.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            StepBeatGoTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StepBeatNavGraph()
                }
            }
        }
    }
}
