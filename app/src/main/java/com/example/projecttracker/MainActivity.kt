package com.example.projecttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.projecttracker.data.db.AppDatabase
import com.example.projecttracker.data.repository.ProjectRepository
import com.example.projecttracker.ui.navigation.BottomNavigationBar
import com.example.projecttracker.ui.screens.dashboard.CreateProjectScreen
import com.example.projecttracker.ui.screens.dashboard.DashboardScreen
import com.example.projecttracker.ui.screens.dashboard.ProjectDetailScreen
import com.example.projecttracker.ui.screens.pomodoro.PomodoroScreen
import com.example.projecttracker.ui.theme.ProjectTrackerTheme
import com.example.projecttracker.viewmodel.AppViewModel
import com.example.projecttracker.viewmodel.AppViewModelFactory

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Pomodoro : Screen("pomodoro")
    data object Notes : Screen("notes")
    data object CreateProject : Screen("createProject")
    data object ProjectDetail : Screen("projectDetail/{projectId}")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚙️ Manual DB and ViewModel setup
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProjectRepository(database.projectDao(), database.taskDao())
        val viewModelFactory = AppViewModelFactory(repository)

        setContent {
            ProjectTrackerTheme {
                MainScreen(viewModelFactory)
            }
        }
    }
}


@Composable
fun MainScreen(factory: AppViewModelFactory) {
    val viewModel: AppViewModel = viewModel(factory = factory)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Pomodoro.route,
        Screen.Notes.route
    )

    Scaffold(
        topBar = { TopBar() },
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(navController, viewModel)
                }
                composable(Screen.Pomodoro.route) {
                    PomodoroScreen()
                }
                composable(Screen.Notes.route) {
                    PlaceholderScreen("Notes")
                }
                composable(Screen.CreateProject.route) {
                    CreateProjectScreen(navController, viewModel)
                }
                composable(
                    route = Screen.ProjectDetail.route,
                    arguments = listOf(navArgument("projectId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getInt("projectId")
                    projectId?.let {
                        ProjectDetailScreen(projectId = it, navController = navController, viewModel = viewModel)
                    } ?: PlaceholderScreen("Invalid Project ID")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("Project Tracker") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun PlaceholderScreen(s: String) {

}
