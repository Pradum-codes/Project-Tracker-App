package com.example.projecttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val techStack: String,
    val githubLink: String?,
    val startDate: Long,
    val endDate: Long,
    val isCompleted: Boolean = false
)

val example = Project(
    title = "Build Tracker App",
    techStack = "Kotlin, Jetpack Compose",
    githubLink = null,
    startDate = System.currentTimeMillis(),
    endDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000, // +7 days
    isCompleted = false
)
