package com.example.projecttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecttracker.data.model.Project
import com.example.projecttracker.data.model.Task
import com.example.projecttracker.data.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ProjectDetailState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)

class AppViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    // Cache for project details
    private val _projectDetailsCache = MutableStateFlow<Map<Int, ProjectDetailState>>(emptyMap())
    private val _projectDetailState = MutableStateFlow(ProjectDetailState())
    val projectDetailState: StateFlow<ProjectDetailState> = _projectDetailState.asStateFlow()

    private val _projectProgressMap = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val projectProgressMap: StateFlow<Map<Int, Float>> = _projectProgressMap.asStateFlow()

    private var currentProjectJob: Job? = null
    private var currentTaskJob: Job? = null

    init {
        viewModelScope.launch {
            repository.getAllProjects().collect { projects ->
                _projects.value = projects
            }
        }
    }

    fun loadProject(projectId: Int) {
        // Check if data is already cached
        val cachedState = _projectDetailsCache.value[projectId]
        if (cachedState != null && !cachedState.isLoading) {
            // Update the state immediately with cached data
            _projectDetailState.value = cachedState
            return
        }

        currentProjectJob?.cancel()
        currentTaskJob?.cancel()

        // Set loading state
        val loadingState = ProjectDetailState(isLoading = true)
        _projectDetailState.value = loadingState
        _projectDetailsCache.value = _projectDetailsCache.value.toMutableMap().apply {
            put(projectId, loadingState)
        }

        currentProjectJob = viewModelScope.launch {
            // Fetch project and tasks once
            val project = repository.getProjectById(projectId).firstOrNull()
            val tasks = repository.getTasksForProject(projectId).firstOrNull() ?: emptyList()
            val newState = ProjectDetailState(project = project, tasks = tasks, isLoading = false)

            // Update cache and state
            _projectDetailsCache.value = _projectDetailsCache.value.toMutableMap().apply {
                put(projectId, newState)
            }
            _projectDetailState.value = newState

            // Continue collecting updates in the background
            combine(
                repository.getProjectById(projectId),
                repository.getTasksForProject(projectId)
            ) { proj, taskList ->
                ProjectDetailState(project = proj, tasks = taskList, isLoading = false)
            }.collect { state ->
                _projectDetailsCache.value = _projectDetailsCache.value.toMutableMap().apply {
                    put(projectId, state)
                }
                // Only update UI state if this project is still selected
                if (_projectDetailState.value.project?.id == projectId) {
                    _projectDetailState.value = state
                }
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
            // Update cache if the project is modified
            _projectDetailsCache.value = _projectDetailsCache.value.toMutableMap().apply {
                get(project.id)?.let { current ->
                    put(project.id, current.copy(project = project))
                }
            }
        }
    }

    fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            // Remove from cache
            _projectDetailsCache.value = _projectDetailsCache.value.toMutableMap().apply {
                remove(projectId)
            }
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

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isDone = !task.isDone))
        }
    }

    fun updateProjectProgress(projectId: Int) {
        viewModelScope.launch {
            val completed = repository.countTaskCompleted(projectId)
            val total = repository.countTaskRemaining(projectId) + completed

            val progress = if (total > 0) {
                (completed.toFloat() / total.toFloat()) * 100f
            } else 0f

            _projectProgressMap.value += (projectId to progress)
        }
    }
}