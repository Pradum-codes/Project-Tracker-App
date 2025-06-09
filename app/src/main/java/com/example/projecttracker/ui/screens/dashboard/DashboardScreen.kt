package com.example.projecttracker.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projecttracker.data.model.Project

@Composable
fun DashboardScreen() {
    val exampleProjects = listOf(
        Project(
            title = "Build Tracker App",
            techStack = "Kotlin, Jetpack Compose",
            githubLink = null,
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
        ),
        Project(
            title = "Learn Coroutines",
            techStack = "Kotlin, Flow",
            githubLink = null,
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000
        )
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {

        Text(
            text = "Your Projects",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn {
            items(exampleProjects) { project ->
                ProjectCard(project = project, onClick = {}, modifier = Modifier)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewDashboard(){
    DashboardScreen()
}
