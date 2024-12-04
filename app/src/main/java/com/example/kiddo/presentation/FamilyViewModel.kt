package com.example.kiddo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kiddo.domain.GetFamilyUseCase
import com.example.kiddo.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FamilyViewModel(
    private val getFamilyUseCase: GetFamilyUseCase
) : ViewModel() {

    private val _familyMembers = MutableStateFlow<List<User>>(emptyList())
    val familyMembers = _familyMembers.asLiveData() // Преобразование в LiveData

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
}