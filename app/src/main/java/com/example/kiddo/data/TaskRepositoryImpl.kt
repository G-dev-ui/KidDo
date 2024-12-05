package com.example.kiddo.data

import com.example.kiddo.domain.api.TaskRepository
import com.example.kiddo.domain.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskRepositoryImpl(
    private val firebaseFirestore: FirebaseFirestore
) : TaskRepository {

    override suspend fun createTask(task: Task): Result<Unit> = try {
        firebaseFirestore.collection("tasks")
            .add(task)
            .await() // Используем корутины для асинхронной работы
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTasks(): Result<List<Task>> = try {
        val snapshot = firebaseFirestore.collection("tasks")
            .get()
            .await()
        val tasks = snapshot.toObjects(Task::class.java)
        Result.success(tasks)
    } catch (e: Exception) {
        Result.failure(e)
    }
}