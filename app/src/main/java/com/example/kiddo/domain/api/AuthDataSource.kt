package com.example.kiddo.domain.api

interface AuthDataSource {
    suspend fun registerUser(email: String, password: String): Result<String>
    suspend fun loginUser(email: String, password: String): Result<String>
}