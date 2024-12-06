package com.example.kiddo.domain.model

data class Task(
    val id: String = "",  // Уникальный идентификатор с дефолтным значением
    val title: String = "",  // Название задачи с дефолтным значением
    val assignedToName: String? = null,  // Имя назначенного пользователя (nullable)
    val assignedToId: String? = null,  // ID назначенного пользователя (nullable)
    val reward: Int = 0,  // Награда с дефолтным значением
    val status: Boolean? = null  // Статус задачи (nullable)
)
