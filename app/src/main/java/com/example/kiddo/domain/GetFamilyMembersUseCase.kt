package com.example.kiddo.domain

import com.example.kiddo.domain.api.UserRepository
import com.example.kiddo.domain.model.User

class GetFamilyMembersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(familyId: String, currentUserId: String): List<User> {
        return userRepository.getFamilyMembers(familyId, currentUserId)
    }
}