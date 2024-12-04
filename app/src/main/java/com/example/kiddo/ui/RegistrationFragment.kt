package com.example.kiddo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kiddo.R
import com.example.kiddo.databinding.FragmentRegistrationBinding
import com.example.kiddo.presentation.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        // Проверяем, авторизован ли пользователь при старте
        if (authViewModel.isUserLoggedIn()) {
            // Если пользователь авторизован, переходим на главный экран
            findNavController().navigate(R.id.action_registrationFragment_to_homeFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).hideBottomNavigationView()

        binding.registerTextView.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_creatingAccFragment)
        }

        // Обработчик кнопки для регистрации или входа
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.editText?.text.toString().trim()
            val password = binding.passwordEditText.editText?.text.toString().trim()

            // Проверка ввода
            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isValidEmail(email)) {
                    authViewModel.loginUser(email, password)
                } else {
                    Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.authState.observe(viewLifecycleOwner, Observer { result ->
            result.fold(
                onSuccess = {
                    // Успешная операция
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registrationFragment_to_homeFragment)
                },
                onFailure = {
                    // Ошибка операции
                    Toast.makeText(context, "Operation failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            )
        })
    }

    // Функция для проверки валидности email
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        return email.matches(emailRegex.toRegex())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


