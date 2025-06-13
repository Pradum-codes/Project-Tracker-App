package com.example.projecttracker.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecttracker.data.model.Task
import com.example.projecttracker.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Int,
    navController: NavHostController,
    viewModel: AppViewModel
) {
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    val project by viewModel.selectedProject.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    var newTaskDescription by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.title ?: "Project Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedProject()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (newTaskDescription.isNotBlank()) {
                        viewModel.saveTask(
                            Task(
                                projectId = projectId,
                                description = newTaskDescription
                            )
                        )
                        newTaskDescription = ""
                    }
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            project?.let { proj ->
                Text("Tech Stack: ${proj.techStack}", style = MaterialTheme.typography.bodyLarge)
                proj.githubLink?.let {
                    Text("GitHub: $it", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text("Tasks", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = newTaskDescription,
                onValueChange = { newTaskDescription = it },
                label = { Text("New Task Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(tasks) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = { viewModel.toggleTaskCompletion(task) }
                        )
                        Text(
                            text = task.description,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Task")
                        }
                    }
                }
            }
        }
    }
}