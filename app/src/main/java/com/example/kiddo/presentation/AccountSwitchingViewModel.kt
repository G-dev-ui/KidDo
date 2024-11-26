package com.example.kiddo.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiddo.domain.CreateChildAccountUseCase
import com.example.kiddo.domain.GetChildrenForParentUseCase
import com.example.kiddo.domain.model.User
import com.example.kiddo.domain.GetUserUseCase
import com.example.kiddo.domain.api.UserRepository
import com.example.kiddo.domain.model.ChildAccount
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountSwitchingViewModel(
    private val userRepository: UserRepository,
    private val getChildrenForParentUseCase: GetChildrenForParentUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val auth: FirebaseAuth,
    private val createChildAccountUseCase: CreateChildAccountUseCase
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    private val _children = MutableLiveData<List<User>>()
    val children: LiveData<List<User>> get() = _children

    private val _currentAccount = MutableLiveData<User?>()  // Изменили на User?
    val currentAccount: LiveData<User?> = _currentAccount  // Соответственно и здесь

    fun fetchUserData() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _error.value = "Ошибка: пользователь не авторизован"
            return
        }

        viewModelScope.launch {
            try {
                // Получаем данные пользователя
                val userData = getUserUseCase(currentUserId)
                _user.value = userData

                // Получаем детей пользователя
                val childrenData = getChildrenForParentUseCase(currentUserId) // Получаем детей
                _children.value = childrenData // Обновляем LiveData с детьми
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных: ${e.message}"
            }
        }
    }

    // Метод для получения детей родителя
    fun fetchChildrenForParent() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _error.value = "Ошибка: пользователь не авторизован"
            return
        }

        viewModelScope.launch {
            try {
                val childrenList = getChildrenForParentUseCase(currentUserId)
                _children.value = childrenList
            } catch (e: Exception) {
                _error.value = "Ошибка получения списка детей: ${e.message}"
            }
        }
    }

    fun createChildAccount(parentId: String, child: ChildAccount, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // Логирование вызова метода
                Log.d("CreateChildAccount", "Attempting to create child account for parent: $parentId")

                val result = createChildAccountUseCase.invoke(parentId, child)

                // Логирование результата
                Log.d("CreateChildAccount", "Child account creation result: $result")

                if (result) {
                    onSuccess()
                } else {
                    // Логирование ошибки, если результат false
                    Log.e("CreateChildAccount", "Error: Failed to create child account")
                    _error.postValue("Ошибка создания аккаунта ребенка")
                }
            } catch (e: Exception) {
                // Логирование ошибки при исключении
                Log.e("CreateChildAccount", "Error: ${e.message}")
                _error.postValue("Ошибка создания аккаунта: ${e.message}")
            }
        }
    }

    fun switchToChildAccount(accountId: String) {
        // Получаем данные для нового аккаунта (можно, например, делать запрос в базу данных)
        viewModelScope.launch {
            try {
                // Проверяем, что метод userRepository.getUserData(accountId) возвращает данные
                val userData = userRepository.getUserData(accountId)
                if (userData != null) {
                    // Обновляем данные пользователя в LiveData
                    _user.value = userData
                } else {
                    _error.value = "Пользователь с ID $accountId не найден"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при переключении аккаунта: ${e.message}"
            }
        }
    }

}