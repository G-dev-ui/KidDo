package com.example.kiddo.data

import android.util.Log
import com.example.kiddo.domain.api.FamilyRepository
import com.example.kiddo.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FamilyRepositoryImpl(
    private val firestore: FirebaseFirestore
) : FamilyRepository {


    // Получение текущего пользователя
   override suspend fun getCurrentUser(userId: String): User? {
        return try {
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (userDoc.exists()) {
                val name = userDoc.getString("name") ?: "Unknown"
                val role = userDoc.getString("role") ?: "Unknown"
                val familyId = userDoc.getString("familyId") ?: "Unknown"

                User(
                    id = userDoc.id,
                    name = name,
                    role = role,
                    familyId = familyId
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching current user: ${e.message}")
            null
        }
    }


    private suspend fun getCurrentUserFamilyId(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return if (currentUser != null) {
            // Получаем данные текущего пользователя из Firestore
            try {
                val userDoc = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                // Возвращаем familyId текущего пользователя
                userDoc.getString("familyId")
            } catch (e: Exception) {
                Log.e("UserRepository", "Error getting current user's familyId: ${e.message}")
                null
            }
        } else {
            Log.e("UserRepository", "No current user found")
            null
        }
    }

    override suspend fun getFamilyMembers(): List<User> {
        return try {
            // Получаем familyId текущего пользователя
            val familyId = getCurrentUserFamilyId()

            if (familyId == null) {
                Log.e("UserRepository", "No familyId found for current user")
                return emptyList()
            }

            // Логируем значение familyId
            Log.d("UserRepository", "Fetching family members for familyId: $familyId")

            // Получаем всех пользователей с указанным familyId
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("familyId", familyId)
                .get()
                .await()

            // Логируем количество документов, найденных в запросе
            Log.d("UserRepository", "Query snapshot fetched: ${querySnapshot.size()}")

            val familyMembers = querySnapshot.documents.mapNotNull { document ->
                val name = document.getString("name") ?: "Unknown"
                val role = document.getString("role") ?: "Unknown"
                val familyIdFromDoc = document.getString("familyId") ?: "Unknown"

                // Логируем каждый найденный документ
                Log.d("UserRepository", "Found family member: $name, $role, Family ID: $familyIdFromDoc")

                User(
                    id = document.id,
                    name = name,
                    role = role,
                    familyId = familyIdFromDoc
                )
            }

            // Логируем количество найденных членов семьи
            Log.d("UserRepository", "Returning ${familyMembers.size} family members")
            familyMembers
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching family members: ${e.message}", e)
            e.printStackTrace()
            emptyList()
        }
    }
}

