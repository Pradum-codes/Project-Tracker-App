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
