package com.example.kiddo.domain

import com.example.kiddo.data.FirebaseAuthDataSource
import com.example.kiddo.domain.api.AuthDataSource

class AuthUseCase(private val authDataSource: AuthDataSource) {

    suspend fun registerUser(email: String, password: String): Result<String> {
        return authDataSource.registerUser(email, password)
    }

    suspend fun loginUser(email: String, password: String): Result<String> {
        return authDataSource.loginUser(email, password)
    }

    // Проверка, авторизован ли пользователь
    fun isUserLoggedIn(): Boolean {
        return (authDataSource as? FirebaseAuthDataSource)?.getCurrentUser() != null
    }
}