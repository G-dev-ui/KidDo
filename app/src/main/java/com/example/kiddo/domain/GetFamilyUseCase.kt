package com.example.kiddo.domain

import com.example.kiddo.domain.api.FamilyRepository
import com.example.kiddo.domain.model.User

class GetFamilyUseCase(private val familyRepository: FamilyRepository) {
    suspend operator fun invoke(): List<User> {
        return familyRepository.getFamilyMembers()
    }
}