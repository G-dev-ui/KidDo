package com.example.kiddo.domain

import com.example.kiddo.domain.api.UserRepository
import com.example.kiddo.domain.model.ChildAccount
import com.example.kiddo.domain.model.User

class CreateChildAccountUseCaseImpl(
    private val userRepository: UserRepository
) : CreateChildAccountUseCase {
    override suspend fun invoke(parentId: String, child: ChildAccount): Boolean {
        val childUser = User(name = child.name, role = "Child") // Преобразуем модель
        return userRepository.createChildAccount(parentId, childUser)
    }
}