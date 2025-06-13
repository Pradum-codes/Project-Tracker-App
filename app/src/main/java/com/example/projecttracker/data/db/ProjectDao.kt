package com.example.projecttracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.projecttracker.data.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM Project")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM Project WHERE id = :projectId")
    fun getProjectById(projectId: Int): Flow<Project?>

    @Insert
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM Project WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Int)
}