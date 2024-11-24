package com.example.kiddo.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiddo.domain.AuthUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val authUseCase: AuthUseCase) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<Result<String>>()
    val authState: LiveData<Result<String>> get() = _authState

    // Регистрация пользователя
    fun registerUser(email: String, password: String, name: String, dateOfBirth: String, role: String) {
        viewModelScope.launch {
            _authState.value = try {
                // Регистрация в Firebase Authentication
                val result = authUseCase.registerUser(email, password)

                // Добавляем данные в Firestore
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val userData = mapOf(
                        "name" to name,
                        "email" to email,
                        "dateOfBirth" to dateOfBirth,
                        "role" to role
                    )
                    firestore.collection("users").document(userId).set(userData).await()
                }

                result // Возвращаем результат успешной регистрации
            } catch (e: Exception) {
                Result.failure(e) // Ошибка при регистрации
            }
        }
    }

    // Вход пользователя
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = try {
                val result = authUseCase.loginUser(email, password)
                result // Возвращаем результат успешного входа
            } catch (e: Exception) {
                Result.failure(e) // Ошибка при входе
            }
        }
    }

    // Получение текущего пользователя
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Выход из аккаунта
    fun logout() {
        auth.signOut()
        _authState.value = Result.success("Logged out successfully")
    }

    // Проверка, авторизован ли пользователь
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}

