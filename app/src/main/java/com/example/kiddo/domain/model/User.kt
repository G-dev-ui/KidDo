package com.example.kiddo.domain.model

data class User(
    val name: String,
    val role: String,
    val parentId: String = ""
)