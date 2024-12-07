package com.example.kiddo.domain

import com.example.kiddo.domain.api.TaskRepository
import com.example.kiddo.domain.model.Task

class RevertTaskStatusUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return taskRepository.revertTaskStatus(task)
    }
}