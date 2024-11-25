package com.example.kiddo.domain

import com.example.kiddo.domain.model.User
import com.example.kiddo.domain.api.UserRepository

class GetUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): User? {
        return userRepository.getUserData(userId)
    }
}