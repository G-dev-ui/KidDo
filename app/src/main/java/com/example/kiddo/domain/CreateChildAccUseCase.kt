package com.example.kiddo.domain

import com.example.kiddo.domain.model.ChildAccount

interface CreateChildAccountUseCase {
    suspend fun invoke(parentId: String, child: ChildAccount): Boolean
}