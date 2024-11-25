package com.example.kiddo.data

import android.util.Log
import com.example.kiddo.domain.model.User
import com.example.kiddo.domain.api.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {
    override suspend fun getUserData(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                User(
                    name = document.getString("name") ?: "Unknown",
                    role = document.getString("role") ?: "Unknown"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createChildAccount(parentId: String, child: User): Boolean {
        return try {
            Log.d("UserRepository", "Creating child account for parent: $parentId")

            // Генерация ID для ребенка
            val childId = firestore.collection("users").document().id

            // Данные для документа ребенка
            val childData = hashMapOf(
                "name" to child.name,
                "role" to "Child", // Роль фиксируем как "Child"
                "parentId" to parentId  // Добавляем родительский ID в ребенка
            )

            // Сохраняем ребенка в Firestore
            firestore.collection("users").document(childId).set(childData).await()
            Log.d("UserRepository", "Child account created with ID: $childId")

            // Получаем ссылку на родителя
            val parentDocRef = firestore.collection("users").document(parentId)

            // Проверяем, существует ли родительский документ
            val parentDocument = parentDocRef.get().await()

            if (parentDocument.exists()) {
                // Если родитель существует, проверяем поле childrenIds
                val childrenIds = parentDocument.get("childrenIds") as? MutableList<String> ?: mutableListOf()

                // Добавляем новый ID ребенка в список
                childrenIds.add(childId)

                // Обновляем родительский документ с новым списком childrenIds
                parentDocRef.update("childrenIds", childrenIds).await()
                Log.d("UserRepository", "Parent account updated with new child ID")

            } else {
                // Если родитель не найден, выбрасываем исключение или логируем ошибку
                Log.e("UserRepository", "Parent document not found.")
                throw Exception("Parent document not found.")
            }

            true

        } catch (e: Exception) {
            // Логируем ошибку, если что-то пошло не так
            Log.e("UserRepository", "Error creating child account: ${e.message}")
            e.printStackTrace()
            false
        }
    }


    // Получение детей для родителя по его parentId
    override suspend fun getChildrenForParent(parentId: String): List<User> {
        return try {
            // Логируем получение ссылки на родителя
            Log.d("UserRepository", "Fetching parent with ID: $parentId")

            // Получаем ссылку на родителя
            val parentDocRef = firestore.collection("users").document(parentId)

            // Логируем запрос данных родителя
            Log.d("UserRepository", "Requesting parent data from Firestore")

            // Получаем данные родителя
            val parentDocument = parentDocRef.get().await()

            if (parentDocument.exists()) {
                // Логируем, что родитель найден
                Log.d("UserRepository", "Parent document found")

                // Извлекаем список childrenIds
                val childrenIds = parentDocument.get("childrenIds") as? List<String> ?: emptyList()

                // Логируем количество детей, которые мы собираемся получить
                Log.d("UserRepository", "Found ${childrenIds.size} children IDs")

                // Получаем данные для каждого ребенка по его ID
                val children = mutableListOf<User>()
                for (childId in childrenIds) {
                    // Логируем получение данных для каждого ребенка
                    Log.d("UserRepository", "Fetching data for child with ID: $childId")

                    val childDocRef = firestore.collection("users").document(childId)
                    val childDocument = childDocRef.get().await()

                    if (childDocument.exists()) {
                        // Логируем данные ребенка
                        val childName = childDocument.getString("name") ?: "Unknown"
                        val childRole = childDocument.getString("role") ?: "Unknown"
                        Log.d("UserRepository", "Found child: Name = $childName, Role = $childRole")

                        // Добавляем данные ребенка в список
                        val child = User(
                            name = childName,
                            role = childRole
                        )
                        children.add(child)
                    } else {
                        // Логируем ошибку, если данные ребенка не найдены
                        Log.e("UserRepository", "Child document with ID $childId not found")
                    }
                }
                // Логируем финальный список детей
                Log.d("UserRepository", "Returning ${children.size} children")
                children
            } else {
                // Логируем ошибку, если родитель не найден
                Log.e("UserRepository", "Parent document with ID $parentId not found")
                emptyList()
            }

        } catch (e: Exception) {
            // Логируем ошибку, если что-то пошло не так
            Log.e("UserRepository", "Error fetching children: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

}
