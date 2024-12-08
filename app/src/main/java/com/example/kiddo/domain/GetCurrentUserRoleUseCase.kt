package com.example.kiddo.domain

import com.example.kiddo.domain.api.UserRepository

class GetCurrentUserRoleUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): String? {
        return userRepository.getCurrentUserRole()
    }
}