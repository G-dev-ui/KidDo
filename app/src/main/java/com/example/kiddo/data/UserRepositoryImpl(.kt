package com.example.kiddo.data

import android.util.Log
import com.example.kiddo.domain.model.User
import com.example.kiddo.domain.api.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {
    override suspend fun getUserData(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                // Используем document.id как уникальный идентификатор
                User(
                    id = document.id,  // Уникальный идентификатор для каждого пользователя
                    name = document.getString("name") ?: "Unknown",
                    role = document.getString("role") ?: "Unknown",
                    familyId = document.getString("familyId")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createChildAccount(
        parentId: String,
        child: User,
        email: String,
        password: String
    ): Boolean {
        return try {
            Log.d("UserRepository", "Creating child account for parent: $parentId with email: $email")

            // 1. Регистрируем пользователя для ребенка с почтой и паролем
            val authResult = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .await()
            val childId = authResult.user?.uid // Получаем ID ребенка

            if (childId == null) {
                Log.e("UserRepository", "Failed to create user for child.")
                return false
            }

            // 2. Получаем данные родителя
            val parentDocRef = firestore.collection("users").document(parentId)
            val parentDocument = parentDocRef.get().await()

            if (!parentDocument.exists()) {
                Log.e("UserRepository", "Parent document not found.")
                throw Exception("Parent document not found.")
            }

            // Извлекаем familyId родителя
            val familyId = parentDocument.getString("familyId")
            if (familyId.isNullOrEmpty()) {
                Log.e("UserRepository", "Parent does not have a familyId.")
                throw Exception("Parent does not have a familyId.")
            }

            // 3. Создаем запись для ребенка в Firestore
            val childData = hashMapOf(
                "name" to child.name,
                "role" to "Child", // Роль фиксируем как "Child"
                "familyId" to familyId, // Присваиваем familyId родителя
                "parentId" to parentId // Добавляем родительский ID в ребенка
            )

            // Сохраняем ребенка в Firestore
            firestore.collection("users").document(childId).set(childData).await()
            Log.d("UserRepository", "Child account created with ID: $childId")

            // 4. Обновляем список детей у родителя
            val childrenIds = parentDocument.get("childrenIds") as? MutableList<String> ?: mutableListOf()
            childrenIds.add(childId)
            parentDocRef.update("childrenIds", childrenIds).await()
            Log.d("UserRepository", "Parent account updated with new child ID")

            true

        } catch (e: Exception) {
            // Логируем ошибку, если что-то пошло не так
            Log.e("UserRepository", "Error creating child account: ${e.message}")
            e.printStackTrace()
            false
        }
    }



    override suspend fun getFamilyMembers(familyId: String, currentUserId: String): List<User> {
        return try {
            Log.d("UserRepository", "Fetching family members for familyId: $familyId, excluding userId: $currentUserId")

            // Получаем всех пользователей с указанным familyId
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("familyId", familyId)
                .get()
                .await()

            // Фильтруем, чтобы исключить текущего пользователя
            val familyMembers = querySnapshot.documents.mapNotNull { document ->
                if (document.id != currentUserId) {
                    // Логируем данные каждого члена семьи
                    Log.d("UserRepository", "Found family member: ${document.getString("name")}")

                    User(
                        id = document.id,
                        name = document.getString("name") ?: "Unknown",
                        role = document.getString("role") ?: "Unknown",
                        familyId = document.getString("familyId")
                    )
                } else {
                    null
                }
            }

            Log.d("UserRepository", "Returning ${familyMembers.size} family members")
            familyMembers
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching family members: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

}
