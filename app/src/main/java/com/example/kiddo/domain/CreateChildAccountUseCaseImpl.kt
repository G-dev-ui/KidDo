package com.example.kiddo.domain

import com.example.kiddo.domain.api.UserRepository
import com.example.kiddo.domain.model.ChildAccount
import com.example.kiddo.domain.model.User

class CreateChildAccountUseCaseImpl(
    private val userRepository: UserRepository,
    private val getUserUseCase: GetUserUseCase // Используем для получения familyId
) : CreateChildAccountUseCase {
    override suspend fun invoke(parentId: String, child: ChildAccount, email: String, password: String): Boolean {
        // Извлекаем данные родителя
        val parentData = getUserUseCase(parentId) ?: throw Exception("Parent not found")
        val familyId = parentData.familyId ?: throw Exception("Parent does not have a familyId")

        // Создаем объект User для ребенка
        val childUser = User(
            id = "", // ID будет сгенерирован автоматически
            name = child.name,
            role = "Child",
            familyId = familyId // Передаем familyId от родителя
        )

        return userRepository.createChildAccount(parentId, childUser, email, password)
    }
}