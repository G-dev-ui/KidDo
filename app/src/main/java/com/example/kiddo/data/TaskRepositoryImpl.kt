package com.example.kiddo.data

import android.util.Log
import com.example.kiddo.domain.api.TaskRepository
import com.example.kiddo.domain.model.Task
import com.google.firebase.firestore.FieldValue
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
            // Ищем документ задачи по полю id
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

            // Проверяем, что assignedToId не равен null перед поиском пользователя
            val assignedToId = task.assignedToId
            if (assignedToId == null) {
                Log.e("CompleteTask", "Assigned user ID is null.")
                return Result.failure(Exception("Assigned user ID is null"))
            }

            // Получаем пользователя, который выполнил задачу по его ID
            val userRef = firebaseFirestore.collection("users").document(assignedToId)

            // Получаем документ пользователя
            val userDocumentSnapshot = userRef.get().await()

            if (!userDocumentSnapshot.exists()) {
                Log.e("CompleteTask", "User not found with ID: $assignedToId")
                return Result.failure(Exception("User not found"))
            }

            // Обновляем количество starCoins пользователя
            userRef.update("starCoins", FieldValue.increment(task.reward.toLong())).await()

            Log.d("CompleteTask", "Reward given to user: ${task.reward} starCoins")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CompleteTask", "Error completing task: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    override suspend fun revertTaskStatus(task: Task): Result<Unit> {
        return try {
            // Ищем документ задачи по полю id
            val taskQuery = firebaseFirestore.collection("tasks")
                .whereEqualTo("id", task.id) // Ищем задачу по значению поля id

            val querySnapshot = taskQuery.get().await()

            if (querySnapshot.isEmpty) {
                Log.e("RevertTaskStatus", "Task not found with ID: ${task.id}")
                return Result.failure(Exception("Task not found"))
            }

            // Получаем ID документа задачи
            val taskDocumentId = querySnapshot.documents.first().id
            Log.d("RevertTaskStatus", "Task Document ID: $taskDocumentId")

            // Получаем ссылку на этот документ
            val taskRef = firebaseFirestore.collection("tasks").document(taskDocumentId)

            // Устанавливаем статус задачи в null
            taskRef.update("status", null).await()

            // Проверяем, что assignedToId не равен null перед поиском пользователя
            val assignedToId = task.assignedToId
            if (assignedToId == null) {
                Log.e("RevertTaskStatus", "Assigned user ID is null.")
                return Result.failure(Exception("Assigned user ID is null"))
            }

            // Получаем пользователя, который был назначен на задачу
            val userRef = firebaseFirestore.collection("users").document(assignedToId)

            // Получаем документ пользователя
            val userDocumentSnapshot = userRef.get().await()

            if (!userDocumentSnapshot.exists()) {
                Log.e("RevertTaskStatus", "User not found with ID: $assignedToId")
                return Result.failure(Exception("User not found"))
            }

            // Уменьшаем количество starCoins пользователя
            userRef.update("starCoins", FieldValue.increment(-task.reward.toLong())).await()

            Log.d("RevertTaskStatus", "Reward reverted for user: ${task.reward} starCoins deducted")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RevertTaskStatus", "Error reverting task status: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }





}

