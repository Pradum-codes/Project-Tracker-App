package com.example.projecttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val description: String,
    val isDone: Boolean = false
)