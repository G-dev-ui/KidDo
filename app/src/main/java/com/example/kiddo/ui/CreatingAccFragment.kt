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
import com.example.kiddo.databinding.FragmentCreatingAccBinding
import com.example.kiddo.presentation.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class CreatingAccFragment : Fragment() {

    private var _binding: FragmentCreatingAccBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatingAccBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).hideBottomNavigationView()

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_creatingAccFragment_to_registrationFragment)
        }

        // Логика для выбора даты рождения
        binding.etDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val dateOfBirth = binding.etDateOfBirth.text.toString().trim()
            val role = "Родитель" // Устанавливаем роль по умолчанию

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && dateOfBirth.isNotEmpty()) {
                authViewModel.registerUser(email, password, name, dateOfBirth, role)
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.authState.observe(viewLifecycleOwner, Observer { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_creatingAccFragment_to_homeFragment)
                },
                onFailure = {
                    Toast.makeText(context, "Ошибка: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            )
        })
    }

    // Функция для отображения DatePickerDialog
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Устанавливаем выбранную дату в поле
                val formattedDate =
                    String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etDateOfBirth.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
