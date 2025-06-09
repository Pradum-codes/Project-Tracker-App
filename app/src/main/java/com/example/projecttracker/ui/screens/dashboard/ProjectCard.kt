package com.example.projecttracker.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projecttracker.data.model.Project
import com.example.projecttracker.data.model.example
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProjectCard(project: Project, onClick: () -> Unit , modifier : Modifier) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val endDateStr = dateFormat.format(Date(project.endDate))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(project.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text("Tech Stack: ${project.techStack}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text("Deadline: $endDateStr", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = if (project.isCompleted) 1f else 0.3f, // Temporary static
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Preview1(){
    ProjectCard(example, onClick = {} ,modifier = Modifier)
}