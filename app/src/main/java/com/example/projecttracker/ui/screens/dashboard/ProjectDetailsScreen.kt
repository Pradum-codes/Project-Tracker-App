package com.example.projecttracker.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        viewModel.loadTask(projectId)
    }
    val project by viewModel.selectedProject.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    var newTaskDescription by rememberSaveable { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.title ?: "Project Details", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedProject()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Project Details Card
            project?.let { proj ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Tech Stack: ${proj.techStack}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        proj.githubLink?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "GitHub: $it",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { /* Add link handling if needed */ }
                            )
                        }
                    }
                }
            }

            // Task Input Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTaskDescription,
                    onValueChange = { newTaskDescription = it },
                    label = { Text("New Task Description") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true
                )
                Button(
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
                    },
                    enabled = newTaskDescription.isNotBlank()
                ) {
                    Text("Add")
                }
            }

            // Tasks Section
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks available. Add a task to get started!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                LazyColumn {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { showDeleteDialog = task }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { task ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete the task \"${task.description}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(task.id)
                    showDeleteDialog = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggleCompletion() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}