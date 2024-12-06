package com.example.kiddo.domain.api

import com.example.kiddo.domain.model.User


interface UserRepository {
    suspend fun getUserData(userId: String): User?
    suspend fun createChildAccount(parentId: String, child: User, email: String, password: String): Boolean
    suspend fun getFamilyMembers(familyId: String, currentUserId: String): List<User> // Новый метод
    suspend fun getUserStarCoins(userId: String): Long // Новый метод
}