package com.example.projecttracker.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projecttracker.data.model.Project
import com.example.projecttracker.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@Composable
fun ProjectCard(
    project: Project,
    onClick: () -> Unit,
    viewModel: AppViewModel,
    modifier: Modifier) {

    LaunchedEffect(project.id) {
        viewModel.updateProjectProgress(project.id)
    }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val endDateStr = dateFormat.format(Date(project.endDate))

    // Calculate time left until deadline
    val currentTime = System.currentTimeMillis()
    val timeDiffMillis = project.endDate - currentTime
    val daysLeft = abs(TimeUnit.MILLISECONDS.toDays(timeDiffMillis))
    val timeLeftText = when {
        timeDiffMillis < 0 -> "$daysLeft days past due"
        daysLeft == 0L -> "Due today"
        else -> "$daysLeft days left"
    }

    val progressMap by viewModel.projectProgressMap.collectAsState()
    val projectProgress = progressMap[project.id] ?: 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
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
                        text = project.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tech Stack: ${project.techStack}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Deadline: $endDateStr",
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
                        text = timeLeftText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (timeDiffMillis < 0) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = projectProgress.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Progress Indicator (below both columns)
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { if (project.isCompleted) 1f else projectProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}