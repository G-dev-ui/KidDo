package com.example.kiddo.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kiddo.R
import com.example.kiddo.databinding.FragmentAccountSwitchingBinding
import com.example.kiddo.domain.model.ChildAccount
import com.example.kiddo.presentation.AccountSwitchingViewModel
import com.example.kiddo.ui.adapter.Account
import com.example.kiddo.ui.adapter.AccountAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar


class AccountSwitchingFragment : Fragment() {

    private var _binding: FragmentAccountSwitchingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountSwitchingViewModel by viewModel()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val accountList = mutableListOf<Account>()
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSwitchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).hideBottomNavigationView()

        setupRecyclerView()
        initializeBottomSheet()

        // Устанавливаем обработчик нажатия на поле даты рождения
        binding.etDateOfBirth.setOnClickListener {
            if (!binding.etDateOfBirth.isEnabled) {
                binding.etDateOfBirth.isEnabled = true // Включаем поле
            }
            showDatePickerDialog()
        }

        // Наблюдаем за изменениями данных пользователя
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvName.text = user.name
                binding.tvRole.text = user.role
            }
        }

        viewModel.familyMembers.observe(viewLifecycleOwner) { familyMembers ->
            if (familyMembers.isNotEmpty()) {
                accountAdapter.addAccounts(familyMembers.map {
                    Account(name = it.name, role = it.role, id = it.id)
                })
            } else {
                Toast.makeText(context, "У вас нет членов семьи.", Toast.LENGTH_SHORT).show()
            }
        }

        // Наблюдаем за ошибками
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Запрос данных
        viewModel.fetchUserData()
        viewModel.fetchFamilyMembers()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Устанавливаем выбранную дату в поле
                val formattedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etDateOfBirth.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun setupRecyclerView() {
        accountAdapter = AccountAdapter(accountList) { account ->
            showLoginDialog(account) // Передаем объект типа Account
        }
        binding.recyclerView.adapter = accountAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initializeBottomSheet() {
        val bottomSheet = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.btnAddAccount.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etName.text.toString()
            val dateOfBirth = binding.etDateOfBirth.text.toString()
            val email = binding.etEmail.text.toString() // Получаем email
            val password = binding.etPassword.text.toString() // Получаем password

            if (name.isBlank() || dateOfBirth.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                val parentId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_parent_id"

                val newChildAccount = ChildAccount(name = name, dateOfBirth = dateOfBirth)

                viewModel.createChildAccount(
                    parentId = parentId,
                    child = newChildAccount,
                    email = email, // Передаем email
                    password = password // Передаем password
                ) {
                    val newAccount = Account(name = newChildAccount.name, role = "Ребёнок", id = "someGeneratedId")
                    accountAdapter.addAccount(newAccount)

                    binding.etName.text.clear()
                    binding.etDateOfBirth.text.clear()
                    binding.etEmail.text.clear() // Очищаем поле email
                    binding.etPassword.text.clear() // Очищаем поле password

                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                    Toast.makeText(context, "Аккаунт создан!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoginDialog(account: Account) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_login, null)
        val etEmail = dialogView.findViewById<EditText>(R.id.et_email)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_password)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Вход в аккаунт: ${account.name}")
            .setView(dialogView)
            .setPositiveButton("Войти") { _, _ ->
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.switchToChildAccount(
                        email = email,
                        password = password,
                        onSuccess = {
                            Toast.makeText(context, "Успешный вход в аккаунт ${account.name}", Toast.LENGTH_SHORT).show()
                            binding.tvName.text = account.name
                            binding.tvRole.text = account.role
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, "Ошибка: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Введите email и пароль!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





