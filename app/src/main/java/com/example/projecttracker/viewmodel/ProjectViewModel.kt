package com.example.projecttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecttracker.data.model.Project
import com.example.projecttracker.data.model.Task
import com.example.projecttracker.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProject.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _projectProgressMap = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val projectProgressMap: StateFlow<Map<Int, Float>> = _projectProgressMap.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllProjects().collect { projects ->
                _projects.value = projects
            }
        }
    }

    fun loadProject(projectId: Int) {
        viewModelScope.launch {
            repository.getProjectById(projectId).collect { project ->
                _selectedProject.value = project
            }
        }
    }

    fun saveProject(project: Project) {
        viewModelScope.launch {
            if (project.id == 0) {
                repository.insertProject(project)
            } else {
                repository.updateProject(project)
            }
        }
    }

    fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            if (task.id == 0) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task)
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun loadTask(projectId: Int) {
        viewModelScope.launch {
            repository.getTasksForProject(projectId).collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isDone = !task.isDone))
        }
    }

    fun clearSelectedProject() {
        _selectedProject.value = null
        _tasks.value = emptyList()
    }


    fun updateProjectProgress(projectId: Int) {
        viewModelScope.launch {
            val completed = repository.countTaskCompleted(projectId)
            val total = repository.countTaskRemaining(projectId) + completed

            val progress = if (total > 0) {
                (completed.toFloat() / total.toFloat()) * 100f
            } else 0f

            _projectProgressMap.value = _projectProgressMap.value + (projectId to progress)
        }
    }
}
