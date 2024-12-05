package com.example.kiddo.domain

import com.example.kiddo.domain.api.TaskRepository
import com.example.kiddo.domain.model.Task

class CreateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return repository.createTask(task)
    }
}