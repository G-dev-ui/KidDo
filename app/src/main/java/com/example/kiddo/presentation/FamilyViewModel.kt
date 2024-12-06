package com.example.kiddo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kiddo.domain.GetFamilyUseCase
import com.example.kiddo.domain.GetUserStarCoinsUseCase
import com.example.kiddo.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FamilyViewModel(
    private val getFamilyUseCase: GetFamilyUseCase,
    private val getUserStarCoinsUseCase: GetUserStarCoinsUseCase // Добавляем UseCase для старкоинов
) : ViewModel() {

    private val _familyMembers = MutableStateFlow<List<User>>(emptyList())
    val familyMembers = _familyMembers.asLiveData() // Преобразование в LiveData

    private val _starCoins = MutableStateFlow<Long>(0L) // Храним старкоины пользователя
    val starCoins: LiveData<Long> = _starCoins.asLiveData() // Обрабатываем старкоины как LiveData

    fun fetchFamilyMembers() {
        viewModelScope.launch {
            try {
                // Вызываем UseCase без familyId, так как он извлекается автоматически в репозитории
                val members = getFamilyUseCase()
                _familyMembers.value = members
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchStarCoins() {
        viewModelScope.launch {
            try {
                val coins = getUserStarCoinsUseCase(userId = String()) // Запрашиваем старкоины без userId
                _starCoins.value = coins // Обновляем значение старкоинов
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}