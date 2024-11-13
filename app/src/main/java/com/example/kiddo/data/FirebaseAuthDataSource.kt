package com.example.kiddo.data

import com.example.kiddo.domain.api.AuthDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource : AuthDataSource {

    private val auth = FirebaseAuth.getInstance()

    // Регистрация пользователя
    override suspend fun registerUser(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Вход пользователя
    override suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Получение текущего пользователя
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}