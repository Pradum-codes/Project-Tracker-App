package com.example.projecttracker.ui.screens.dashboard

import android.util.Log
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projecttracker.viewmodel.AppViewModel

@Composable
fun ProjectCard(
    projectId: Int,
    onClick: () -> Unit,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val cardStates by viewModel.projectCardStates.collectAsState()
    val cardState = cardStates[projectId] ?: return // Exit if state not available
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Project") },
            text = { Text("Are you sure you want to delete the project \"${cardState.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProject(projectId)
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showDeleteDialog = true }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // First Column: Project Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = cardState.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tech Stack: ${cardState.techStack}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Deadline: ${cardState.endDateStr}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Second Column: Time Left
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = cardState.timeLeftText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = cardState.timeLeftColor
                    )
                    Text(
                        text = "${cardState.progress.toInt()}%",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Progress Indicator
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { if (cardState.isCompleted) 1f else cardState.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            ) { Log.d("Indicator", "Indicator for project ${cardState.projectId}") }
        }
    }
}