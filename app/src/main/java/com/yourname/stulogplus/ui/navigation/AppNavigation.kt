package com.yourname.stulogplus.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.yourname.stulogplus.StulogPlusApp
import com.yourname.stulogplus.data.repository.StudyRepository
import com.yourname.stulogplus.ui.screens.*

private data class Destination(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as StulogPlusApp
    val repo = remember { StudyRepository(app.database) }
    val destinations = listOf(
        Destination("timer", "集中", Icons.Outlined.Timer),
        Destination("tasks", "やること", Icons.Outlined.CheckCircle),
        Destination("subjects", "教材", Icons.Outlined.MenuBook),
        Destination("records", "記録", Icons.Outlined.BarChart),
        Destination("settings", "設定", Icons.Outlined.Settings)
    )
    Scaffold(bottomBar = {
        NavigationBar {
            val entry by nav.currentBackStackEntryAsState()
            destinations.forEach { item ->
                NavigationBarItem(selected = entry?.destination?.route == item.route, onClick = {
                    nav.navigate(item.route) { popUpTo(nav.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true }
                }, icon = { Icon(item.icon, contentDescription = item.title) }, label = { Text(item.title) })
            }
        }
    }) { padding ->
        NavHost(navController = nav, startDestination = "timer", modifier = Modifier.padding(padding)) {
            composable("timer") { TimerScreen(repo) }
            composable("tasks") { TasksScreen(repo) }
            composable("subjects") { SubjectsScreen(repo) }
            composable("records") { RecordsScreen(repo) }
            composable("settings") { SettingsScreen(repo) }
        }
    }
}
