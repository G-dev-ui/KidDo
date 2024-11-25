package com.example.kiddo.domain

import com.example.kiddo.domain.api.UserRepository
import com.example.kiddo.domain.model.User

// UseCase для получения списка детей родителя
class GetChildrenForParentUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(parentId: String): List<User> {
        return userRepository.getChildrenForParent(parentId)
    }
}