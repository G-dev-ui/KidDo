package com.example.kiddo.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiddo.domain.CreateTaskUseCase
import com.example.kiddo.domain.GetTasksUseCase
import com.example.kiddo.domain.model.Task
import kotlinx.coroutines.launch

class TaskViewModel(
    private val createTaskUseCase: CreateTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>?>()
    val tasks: LiveData<List<Task>?> = _tasks

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun createTask(task: Task) {
        viewModelScope.launch {
            try {
                val result = createTaskUseCase(task)
                if (result.isSuccess) {
                    loadTasks() // Обновляем список задач
                } else {
                    _error.value = result.exceptionOrNull()?.localizedMessage
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при создании задачи: ${e.localizedMessage}"
                Log.e("TaskViewModel", "Error while loading tasks", e)
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            try {
                val result = getTasksUseCase()
                if (result.isSuccess) {
                    _tasks.value = result.getOrNull()
                } else {
                    _error.value = result.exceptionOrNull()?.localizedMessage
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при загрузке задач: ${e.localizedMessage}"
            }
        }
    }
}
