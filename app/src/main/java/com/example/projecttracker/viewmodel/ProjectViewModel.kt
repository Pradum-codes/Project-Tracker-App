package com.example.projecttracker.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecttracker.data.model.Project
import com.example.projecttracker.data.model.Task
import com.example.projecttracker.data.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

data class ProjectDetailState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)

data class ProjectCardState(
    val projectId: Int,
    val title: String,
    val techStack: String,
    val endDateStr: String,
    val timeLeftText: String,
    val timeLeftColor: Color,
    val progress: Float,
    val isCompleted: Boolean
)

class AppViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _projectDetailState = MutableStateFlow(ProjectDetailState())
    val projectDetailState: StateFlow<ProjectDetailState> = _projectDetailState.asStateFlow()

    private val _projectDetailsCache = MutableStateFlow<Map<Int, ProjectDetailState>>(emptyMap())

    private val _projectCardStates = MutableStateFlow<Map<Int, ProjectCardState>>(emptyMap())
    val projectCardStates: StateFlow<Map<Int, ProjectCardState>> = _projectCardStates.asStateFlow()

    init {
        observeProjectsAndTasks()
    }

    private fun observeProjectsAndTasks() {
        viewModelScope.launch {
            combine(
                repository.getAllProjects(),
                repository.getAllTasks()
            ) { projects, tasks ->
                _projects.value = projects // Update the public project list

                val groupedTasks = tasks.groupBy { it.projectId }
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val currentTime = System.currentTimeMillis()

                projects.associate { project ->
                    val projectTasks = groupedTasks[project.id] ?: emptyList()
                    val completed = projectTasks.count { it.isDone }
                    val total = projectTasks.size
                    val progress = if (total > 0) (completed.toFloat() / total) * 100f else 0f

                    val endDateStr = dateFormat.format(Date(project.endDate))
                    val timeDiffMillis = project.endDate - currentTime
                    val daysLeft = abs(TimeUnit.MILLISECONDS.toDays(timeDiffMillis))
                    val timeLeftText = when {
                        timeDiffMillis < 0 -> "$daysLeft days past due"
                        daysLeft == 0L -> "Due today"
                        else -> "$daysLeft days left"
                    }
                    val timeLeftColor = if (timeDiffMillis < 0) {
                        Color(0xFFE57373)
                    } else {
                        Color(0xFF1976D2)
                    }

                    project.id to ProjectCardState(
                        projectId = project.id,
                        title = project.title,
                        techStack = project.techStack,
                        endDateStr = endDateStr,
                        timeLeftText = timeLeftText,
                        timeLeftColor = timeLeftColor,
                        progress = progress,
                        isCompleted = project.isCompleted
                    )
                }
            }.collect { newCardStates ->
                _projectCardStates.value = newCardStates
                Log.d("AppViewModel", "Updated projectCardStates: ${newCardStates.size}")
            }
        }
    }

    fun loadProject(projectId: Int) {
        val cached = _projectDetailsCache.value[projectId]
        if (cached != null && !cached.isLoading) {
            _projectDetailState.value = cached
            return
        }

        _projectDetailsCache.value = _projectDetailsCache.value.toMutableMap().apply {
            put(projectId, ProjectDetailState(isLoading = true))
        }
        _projectDetailState.value = ProjectDetailState(isLoading = true)

        viewModelScope.launch {
            combine(
                repository.getProjectById(projectId),
                repository.getTasksForProject(projectId)
            ) { project, tasks ->
                ProjectDetailState(project = project, tasks = tasks, isLoading = false)
            }.collect { state ->
                _projectDetailsCache.value = _projectDetailsCache.value.toMutableMap().apply {
                    put(projectId, state)
                }
                if (_projectDetailState.value.project?.id == projectId || _projectDetailState.value.isLoading) {
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
        }
    }

    fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
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
            Log.d("AppViewModel", "Toggling task ${task.id}, isDone=${task.isDone}")
            try {
                repository.updateTask(task.copy(isDone = !task.isDone))
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error toggling task ${task.id}: ${e.message}")
            }
        }
    }
}
