package com.example.projecttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProjectRepository(database.projectDao(), database.taskDao())
        val viewModelFactory = AppViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory)[AppViewModel::class.java]

        setContent {
            ProjectTrackerTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: AppViewModel) {
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
                startDestination = Screen.Dashboard.route,
                enterTransition = { fadeIn(animationSpec = tween(600)) },
                exitTransition = { fadeOut(animationSpec = tween(600)) },
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(navController, viewModel)
                }
                composable(Screen.Pomodoro.route) {
                    PomodoroScreen()
                }
                composable(Screen.Notes.route) {
                    PlaceholderScreen("Notes Coming Soon")
                }
                composable(Screen.CreateProject.route) {
                    CreateProjectScreen(navController, viewModel)
                }
                composable(
                    route = Screen.ProjectDetail.route,
                    arguments = listOf(navArgument("projectId") { type = NavType.IntType }),
                    enterTransition = { fadeIn( animationSpec = tween(600)) },
                    exitTransition = { fadeOut( animationSpec = tween(600)) }
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getInt("projectId")
                    projectId?.let {
//                        LaunchedEffect(projectId) {
//                            viewModel.loadProject(projectId)
//                            viewModel.loadTask(projectId)
//                        }
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
fun PlaceholderScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}
