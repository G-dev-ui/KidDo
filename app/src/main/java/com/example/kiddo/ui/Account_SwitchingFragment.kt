package com.example.kiddo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kiddo.databinding.FragmentAccountSwitchingBinding
import com.example.kiddo.domain.model.ChildAccount
import com.example.kiddo.presentation.AccountSwitchingViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel


class AccountSwitchingFragment : Fragment() {

    private var _binding: FragmentAccountSwitchingBinding? = null
    private val binding get() = _binding!!

    // Получаем ViewModel через Koin
    private val viewModel: AccountSwitchingViewModel by viewModel()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    // Инициализируем список аккаунтов и адаптер
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

        // Настройка RecyclerView
        setupRecyclerView()



        // Инициализация BottomSheetBehavior
        initializeBottomSheet()

        // Наблюдаем за изменениями данных пользователя
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvName.text = user.name
                binding.tvRole.text = user.role
            }
        }

        // Наблюдаем за детьми
        viewModel.children.observe(viewLifecycleOwner) { children ->
            if (children.isNotEmpty()) {
                // Обновляем список через адаптер после получения данных о детях
                accountAdapter.addAccounts(children.map { Account(name = it.name, role = it.role) })
            } else {
                // Если детей нет, можно отобразить сообщение
                Toast.makeText(context, "У вас нет детей.", Toast.LENGTH_SHORT).show()
            }
        }

        // Наблюдаем за ошибками
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Запрос данных
        viewModel.fetchUserData()


    }

    private fun setupRecyclerView() {
        // Создаём адаптер и присоединяем его к RecyclerView
        accountAdapter = AccountAdapter(accountList)
        binding.recyclerView.adapter = accountAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initializeBottomSheet() {
        // Найти BottomSheet
        val bottomSheet = binding.bottomSheet // Ваш LinearLayout в XML с id `bottomSheet`
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Установить состояние BottomSheet в скрытое при старте
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // Настроить кнопку открытия BottomSheet
        binding.btnAddAccount.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etName.text.toString()
            val dateOfBirth = binding.etDateOfBirth.text.toString() // Используем дату рождения

            if (name.isBlank() || dateOfBirth.isBlank()) {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                // Получаем parentId (например, из FirebaseAuth или других источников)
                val parentId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_parent_id" // Если нет, используем дефолтный ID

                // Создаем объект ChildAccount
                val newChildAccount = ChildAccount(name = name, dateOfBirth = dateOfBirth)

                // Используем ViewModel для создания аккаунта
                viewModel.createChildAccount(parentId = parentId, child = newChildAccount) {
                    // Обновляем список через адаптер после успешного создания
                    val newAccount = Account(name = newChildAccount.name, role = "Ребёнок")
                    accountAdapter.addAccount(newAccount)

                    // Очистить поля ввода
                    binding.etName.text.clear()
                    binding.etDateOfBirth.text.clear()

                    // Скрыть BottomSheet
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                    Toast.makeText(context, "Аккаунт создан!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



