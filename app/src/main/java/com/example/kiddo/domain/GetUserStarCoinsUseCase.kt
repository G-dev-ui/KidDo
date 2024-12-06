package com.example.kiddo.domain

import com.example.kiddo.domain.api.UserRepository

class GetUserStarCoinsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Long {
        return userRepository.getUserStarCoins(userId)
    }
}