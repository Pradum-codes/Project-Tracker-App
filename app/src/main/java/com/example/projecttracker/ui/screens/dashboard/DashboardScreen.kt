package com.example.projecttracker.ui.screens.dashboard

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecttracker.Screen
import com.example.projecttracker.viewmodel.AppViewModel

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val projects = viewModel.projects.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = "Your Projects",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f), flingBehavior = ScrollableDefaults.flingBehavior() ) {
                items(items = projects, key = {it.id}) { project ->
                    ProjectCard(
                        projectId = project.id,
                        onClick = {
                            navController.navigate("projectDetail/${project.id}")
                        },
                        viewModel = viewModel,
                        modifier = Modifier
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.CreateProject.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Project")
        }
    }
}