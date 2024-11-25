package com.example.kiddo.domain.api

import com.example.kiddo.domain.model.User


interface UserRepository {
    suspend fun getUserData(userId: String): User?
    suspend fun createChildAccount(parentId: String, child: User): Boolean
    suspend fun getChildrenForParent(parentId: String): List<User>
}