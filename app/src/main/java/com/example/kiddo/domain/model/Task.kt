package com.example.kiddo.domain.model

data class Task(
    val id: String = "",  // Значения по умолчанию
    val title: String = "",
    val assignedToName: String? = null,
    val reward: Int = 0,
    val status: Boolean? = null
)
