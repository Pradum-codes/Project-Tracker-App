package com.example.projecttracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.projecttracker.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task WHERE projectId = :projectId")
    fun getTasksForProject(projectId: Int): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<Task?>

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM Task WHERE projectId = :projectId")
    suspend fun deleteTasksForProject(projectId: Int)
}