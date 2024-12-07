package com.example.kiddo.domain.api

import com.example.kiddo.domain.model.Task

interface TaskRepository {
    suspend fun createTask(task: Task): Result<Unit>
    suspend fun getTasks(): Result<List<Task>>
    suspend fun updateTask(task: Task): Result<Unit> // Новый метод
    suspend fun completeTask(task: Task): Result<Unit> // Новый метод
    suspend fun revertTaskStatus(task: Task): Result<Unit>
}