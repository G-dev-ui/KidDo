package com.example.kiddo.domain

import com.example.kiddo.domain.api.FamilyRepository
import com.example.kiddo.domain.model.User

class GetCurrentUserUseCase(
    private val familyRepository: FamilyRepository
) {
    suspend fun execute(userId: String): User? {
        return familyRepository.getCurrentUser(userId)
    }
}