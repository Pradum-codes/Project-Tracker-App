package com.example.projecttracker.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecttracker.Screen
import com.example.projecttracker.data.model.Project
import com.example.projecttracker.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    var title by remember { mutableStateOf("") }
    var techStack by remember { mutableStateOf("") }
    var githubLink by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Project") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = techStack,
                onValueChange = { techStack = it },
                label = { Text("Tech Stack") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = githubLink,
                onValueChange = { githubLink = it },
                label = { Text("GitHub Link (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val project = Project(
                        title = title,
                        techStack = techStack,
                        githubLink = if (githubLink.isBlank()) null else githubLink,
                        startDate = startDate,
                        endDate = endDate
                    )
                    viewModel.saveProject(project)
                    navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && techStack.isNotBlank()
            ) {
                Text("Save Project")
            }
        }
    }
}