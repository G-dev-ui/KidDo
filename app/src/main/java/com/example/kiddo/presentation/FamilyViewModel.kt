package com.example.kiddo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kiddo.domain.GetCurrentUserRoleUseCase
import com.example.kiddo.domain.GetCurrentUserUseCase
import com.example.kiddo.domain.GetFamilyUseCase
import com.example.kiddo.domain.GetUserStarCoinsUseCase
import com.example.kiddo.domain.GetUserUseCase
import com.example.kiddo.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FamilyViewModel(
    private val getFamilyUseCase: GetFamilyUseCase,
    private val getUserStarCoinsUseCase: GetUserStarCoinsUseCase,
    private val getCurrentUserRoleUseCase: GetCurrentUserRoleUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _familyMembers = MutableStateFlow<List<User>>(emptyList())
    val familyMembers = _familyMembers.asLiveData()

    private val _starCoins = MutableStateFlow<Long>(0L)
    val starCoins: LiveData<Long> = _starCoins.asLiveData()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asLiveData()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asLiveData()


    fun fetchFamilyMembers() {
        viewModelScope.launch {
            try {
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
                val coins = getUserStarCoinsUseCase(userId = String())
                _starCoins.value = coins
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUserRole() {
        viewModelScope.launch {
            try {
                val role = getCurrentUserRoleUseCase()
                _userRole.value = role
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchCurrentUser(userId: String) {
        viewModelScope.launch {
            try {
                val user = getCurrentUserUseCase.execute(userId) // Используем новый UseCase
                _currentUser.value = user
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
