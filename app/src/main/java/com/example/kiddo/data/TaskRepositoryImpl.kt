package com.example.kiddo.data

import android.util.Log
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
            .await()
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

    override suspend fun updateTask(task: Task): Result<Unit> = try {
        firebaseFirestore.collection("tasks")
            .document(task.id) // Убедитесь, что у задачи есть уникальный идентификатор
            .set(task)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun completeTask(task: Task): Result<Unit> {
        return try {
            // Ищем документ по полю id, которое находится в данных документа
            val taskQuery = firebaseFirestore.collection("tasks")
                .whereEqualTo("id", task.id) // Ищем задачу по значению поля id

            val querySnapshot = taskQuery.get().await()

            if (querySnapshot.isEmpty) {
                Log.e("CompleteTask", "Task not found with ID: ${task.id}")
                return Result.failure(Exception("Task not found"))
            }

            // Получаем ID документа задачи
            val taskDocumentId = querySnapshot.documents.first().id
            Log.d("CompleteTask", "Task Document ID: $taskDocumentId")

            // Получаем ссылку на этот документ
            val taskRef = firebaseFirestore.collection("tasks").document(taskDocumentId)

            // Обновляем статус задачи
            taskRef.update("status", true).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CompleteTask", "Error completing task: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }



}

