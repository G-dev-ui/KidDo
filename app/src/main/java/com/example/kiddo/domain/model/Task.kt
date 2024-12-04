package com.example.kiddo.domain.model

data class Task(
    val id: String,
    val title: String,
    val assignedToName: String?,
    val reward: Int
)
