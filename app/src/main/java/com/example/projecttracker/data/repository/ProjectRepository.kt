package com.example.projecttracker.data.repository

import com.example.projecttracker.data.db.ProjectDao
import com.example.projecttracker.data.db.TaskDao
import com.example.projecttracker.data.model.Project
import com.example.projecttracker.data.model.Task
import kotlinx.coroutines.flow.Flow

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao
) {
    // Project operations
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()

    fun getProjectById(projectId: Int): Flow<Project?> = projectDao.getProjectById(projectId)

    suspend fun insertProject(project: Project) {
        projectDao.insertProject(project)
    }

    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project)
    }

    suspend fun deleteProject(projectId: Int) {
        taskDao.deleteTasksForProject(projectId) // Delete associated tasks first
        projectDao.deleteProjectById(projectId)
    }

    // Task operations
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksForProject(projectId: Int): Flow<List<Task>> = taskDao.getTasksForProject(projectId)

    fun getTaskById(taskId: Int): Flow<Task?> = taskDao.getTaskById(taskId)

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun countTaskCompleted(projectId: Int): Int {
        return taskDao.countTaskCompleted(projectId)
    }

    suspend fun countTaskRemaining(projectId: Int): Int {
        return taskDao.countTaskRemaining(projectId)
    }
}