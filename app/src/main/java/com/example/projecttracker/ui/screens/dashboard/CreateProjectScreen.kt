package com.example.projecttracker.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.projecttracker.Screen
import com.example.projecttracker.data.model.Project
import com.example.projecttracker.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

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

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Project") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

            // Start Date
            OutlinedTextField(
                value = formatDate(startDate),
                onValueChange = {}, // No-op since we're not allowing manual text input
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select start date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStartDatePicker = true },
                label = { Text("Start Date") }
            )

            // End Date
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = formatDate(endDate),
                onValueChange = {}, // No-op since we're not allowing manual text input
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select end date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEndDatePicker = true },
                label = { Text("End Date") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val project = Project(
                        title = title,
                        techStack = techStack,
                        githubLink = githubLink.takeIf { it.isNotBlank() },
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

            // Start Date Picker Dialog
            if (showStartDatePicker) {
                DatePickerModalInput(
                    initialSelectedDateMillis = startDate,
                    onDateSelected = { date ->
                        date?.let { startDate = it }
                        showStartDatePicker = false
                    },
                    onDismiss = { showStartDatePicker = false }
                )
            }

            // End Date Picker Dialog
            if (showEndDatePicker) {
                DatePickerModalInput(
                    initialSelectedDateMillis = endDate,
                    onDateSelected = { date ->
                        date?.let { endDate = it }
                        showEndDatePicker = false
                    },
                    onDismiss = { showEndDatePicker = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    initialSelectedDateMillis: Long,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}