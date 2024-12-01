package com.example.kiddo.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiddo.domain.CreateChildAccountUseCase
import com.example.kiddo.domain.GetFamilyMembersUseCase
import com.example.kiddo.domain.model.User
import com.example.kiddo.domain.GetUserUseCase
import com.example.kiddo.domain.api.UserRepository
import com.example.kiddo.domain.model.ChildAccount
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountSwitchingViewModel(
    private val userRepository: UserRepository,
    private val getUserUseCase: GetUserUseCase,
    private val auth: FirebaseAuth,
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase,
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


    private val _familyMembers = MutableLiveData<List<User>>()
    val familyMembers: LiveData<List<User>> get() = _familyMembers

    fun fetchUserData() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _error.value = "Ошибка: пользователь не авторизован"
            return
        }

        viewModelScope.launch {
            try {
                // Получаем данные текущего пользователя
                val userData = getUserUseCase(currentUserId)
                _user.value = userData

                // Проверяем наличие familyId
                val familyId = userData?.familyId
                if (familyId == null) {
                    _error.value = "Ошибка: familyId не найден"
                    return@launch
                }

                // Получаем членов семьи и обновляем LiveData
                val familyMembers = getFamilyMembersUseCase(familyId, currentUserId)
                _familyMembers.value = familyMembers
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных: ${e.message}"
            }
        }
    }

    fun fetchFamilyMembers() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _error.value = "Ошибка: пользователь не авторизован"
            return
        }

        viewModelScope.launch {
            try {
                val currentUserData = getUserUseCase(currentUserId)
                if (currentUserData?.familyId == null) {
                    _error.value = "Ошибка: familyId не найден"
                    return@launch
                }

                val members = getFamilyMembersUseCase(currentUserData.familyId, currentUserId)
                _familyMembers.value = members
            } catch (e: Exception) {
                _error.value = "Ошибка получения членов семьи: ${e.message}"
            }
        }
    }

    fun createChildAccount(
        parentId: String,
        child: ChildAccount,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Логирование вызова метода
                Log.d("CreateChildAccount", "Attempting to create child account for parent: $parentId")

                // Передаем email и password
                val result = createChildAccountUseCase.invoke(parentId, child, email, password)

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

    fun switchToChildAccount(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                viewModelScope.launch {
                                    val userData = userRepository.getUserData(userId)
                                    if (userData != null) {
                                        _user.value = userData
                                        fetchFamilyMembers()
                                        onSuccess()
                                    } else {
                                        onError("Не удалось получить данные пользователя.")
                                    }
                                }
                            } else {
                                onError("Не удалось получить ID пользователя.")
                            }
                        } else {
                            onError(task.exception?.message ?: "Ошибка входа.")
                        }
                    }
            } catch (e: Exception) {
                onError("Ошибка при входе: ${e.message}")
            }
        }
    }

}