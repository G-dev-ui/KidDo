package com.example.kiddo.domain.api

import com.example.kiddo.domain.model.User

interface FamilyRepository {
    suspend fun getFamilyMembers(): List<User>
    suspend fun getCurrentUser(userId: String): User?
}