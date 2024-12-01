package com.example.kiddo.domain.model

data class User(
    val id: String,
    val name: String,
    val role: String,
    val familyId: String?,
    val parentId: String = ""
)