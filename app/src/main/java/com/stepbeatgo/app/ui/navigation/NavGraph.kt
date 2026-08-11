package com.stepbeatgo.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.stepbeatgo.app.ui.screens.achievements.AchievementsScreen
import com.stepbeatgo.app.ui.screens.favorites.FavoritesScreen
import com.stepbeatgo.app.ui.screens.genre.GenreScreen
import com.stepbeatgo.app.ui.screens.history.HistoryScreen
import com.stepbeatgo.app.ui.screens.home.HomeScreen
import com.stepbeatgo.app.ui.screens.metronome.MetronomeScreen
import com.stepbeatgo.app.ui.screens.mode.CustomBpmScreen
import com.stepbeatgo.app.ui.screens.mode.ModeScreen
import com.stepbeatgo.app.ui.screens.playlist.PlaylistScreen
import com.stepbeatgo.app.ui.screens.result.ResultScreen
import com.stepbeatgo.app.ui.screens.settings.SettingsScreen
import com.stepbeatgo.app.ui.viewmodel.CalculationViewModel
import com.stepbeatgo.app.ui.viewmodel.SoundtrackMode

private data class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomItems = listOf(
    BottomItem(Destinations.HOME, "Главная", Icons.Filled.Home),
    BottomItem(Destinations.FAVORITES, "Избранное", Icons.Filled.Favorite),
    BottomItem(Destinations.HISTORY, "История", Icons.Filled.History),
    BottomItem(Destinations.SETTINGS, "Настройки", Icons.Filled.Settings)
)

@Composable
fun StepBeatNavGraph() {
    val navController = rememberNavController()
    val calcVm: CalculationViewModel = viewModel()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            NavigationBar {
                bottomItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destinations.HOME) {
                HomeScreen { name, baseline, distance ->
                    calcVm.reset()
                    calcVm.setRoute(name, baseline, distance)
                    navController.navigate(Destinations.MODE)
                }
            }

            composable(Destinations.MODE) {
                ModeScreen { mode ->
                    calcVm.mode = mode
                    when (mode) {
                        SoundtrackMode.GENRE -> navController.navigate(Destinations.GENRE)
                        SoundtrackMode.PLAYLIST -> navController.navigate(Destinations.PLAYLIST)
                        SoundtrackMode.CUSTOM_BPM -> navController.navigate(Destinations.CUSTOM_BPM)
                    }
                }
            }

            composable(Destinations.GENRE) {
                GenreScreen { genre ->
                    calcVm.selectedGenre = genre
                    navController.navigate(Destinations.RESULT)
                }
            }

            composable(Destinations.CUSTOM_BPM) {
                CustomBpmScreen(initialBpm = calcVm.customBpm) { bpm ->
                    calcVm.customBpm = bpm
                    navController.navigate(Destinations.RESULT)
                }
            }

            composable(Destinations.PLAYLIST) {
                PlaylistScreen(calcVm = calcVm) {
                    navController.navigate(Destinations.RESULT)
                }
            }

            composable(Destinations.RESULT) {
                ResultScreen(calcVm = calcVm) { bpm ->
                    navController.navigate(Destinations.metronome(bpm))
                }
            }

            composable(
                route = Destinations.METRONOME,
                arguments = listOf(navArgument("bpm") { type = NavType.IntType })
            ) { backStackEntry ->
                val bpm = backStackEntry.arguments?.getInt("bpm") ?: 120
                MetronomeScreen(bpm = bpm)
            }

            composable(Destinations.FAVORITES) { FavoritesScreen() }
            composable(Destinations.HISTORY) { HistoryScreen() }
            composable(Destinations.SETTINGS) { SettingsScreen() }
            composable(Destinations.ACHIEVEMENTS) { AchievementsScreen() }
        }
    }
}
