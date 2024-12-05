package com.example.kiddo.domain

import com.example.kiddo.domain.api.TaskRepository
import com.example.kiddo.domain.model.Task

class GetTasksUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(): Result<List<Task>> {
        return repository.getTasks()
    }
}