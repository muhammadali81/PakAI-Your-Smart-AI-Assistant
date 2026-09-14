package com.example.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TaskEntity
import com.example.data.repository.PakAiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL, PENDING, COMPLETED
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PakAiRepository(application)

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val allTasksFlow = repository.getAllTasks()

    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        allTasksFlow,
        _selectedFilter,
        _selectedCategory
    ) { tasks, filter, category ->
        tasks.filter { task ->
            val matchesFilter = when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.PENDING -> !task.isCompleted
                TaskFilter.COMPLETED -> task.isCompleted
            }
            val matchesCategory = if (category == "All") true else task.category.equals(category, ignoreCase = true)
            matchesFilter && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = repository.getTotalTasksCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedCount: StateFlow<Int> = repository.getCompletedTasksCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isGeneratingAiTasks = MutableStateFlow(false)
    val isGeneratingAiTasks: StateFlow<Boolean> = _isGeneratingAiTasks.asStateFlow()

    private val _toastNotification = MutableStateFlow<String?>(null)
    val toastNotification: StateFlow<String?> = _toastNotification.asStateFlow()

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun clearToast() {
        _toastNotification.value = null
    }

    fun addTask(title: String, description: String, category: String, priority: String, dueDate: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = TaskEntity(
                title = title.trim(),
                description = description.trim(),
                category = category,
                priority = priority,
                dueDate = dueDate.trim(),
                source = "manual"
            )
            repository.insertTask(task)
            _toastNotification.value = "Task created!"
        }
    }

    fun toggleTask(id: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(id, !currentCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _toastNotification.value = "Task deleted"
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
            _toastNotification.value = "Completed tasks cleared"
        }
    }

    fun generateAiTasks(goal: String) {
        if (goal.isBlank() || _isGeneratingAiTasks.value) return
        viewModelScope.launch {
            _isGeneratingAiTasks.value = true
            val result = repository.generateTasksFromPrompt(goal)
            if (result.isSuccess) {
                val tasks = result.getOrNull() ?: emptyList()
                if (tasks.isNotEmpty()) {
                    repository.insertTasks(tasks)
                    _toastNotification.value = "Pak AI created ${tasks.size} tasks for you!"
                } else {
                    _toastNotification.value = "No specific tasks could be parsed. Try a more detailed goal."
                }
            } else {
                _toastNotification.value = result.exceptionOrNull()?.message ?: "Failed to generate tasks"
            }
            _isGeneratingAiTasks.value = false
        }
    }
}
