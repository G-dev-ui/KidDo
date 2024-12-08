package com.example.kiddo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kiddo.R
import com.example.kiddo.databinding.FragmentHomeBinding
import com.example.kiddo.presentation.AuthViewModel
import com.example.kiddo.presentation.FamilyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModel() // Подключаем AuthViewModel
    private val familyViewModel: FamilyViewModel by viewModel() // Подключаем FamilyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Проверка авторизации пользователя
        checkUserAuthentication()

        (activity as MainActivity).showBottomNavigationView()

        binding.btnAcc.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_accountSwitchingFragment)
        }

        // Показать старкоины текущего пользователя
        familyViewModel.fetchStarCoins() // Запрашиваем старкоины
        familyViewModel.starCoins.observe(viewLifecycleOwner) { starCoins ->
            binding.starCoinCount.text = "StarCoins: $starCoins" // Отображаем старкоины в UI
        }



        // Обработчик нажатия на кнопку "Выход"
        binding.outBtn.setOnClickListener {
            // Вызываем метод logout() из AuthViewModel
            authViewModel.logout()

            // После выхода, можно, например, перенаправить пользователя на экран входа/регистрации
            findNavController().navigate(R.id.action_homeFragment_to_registrationFragment)

            // Показать уведомление, что пользователь вышел
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun checkUserAuthentication() {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser == null) {
            // Пользователь не авторизован, перенаправляем на экран регистрации
            findNavController().navigate(R.id.action_homeFragment_to_registrationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
