package com.example.kiddo.ui

import android.os.Bundle
import android.util.Log
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

        setupRecyclerView()
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
                accountAdapter.addAccounts(children.map {
                    Account(name = it.name, role = it.role, id = it.id) // Убедитесь, что id передается
                })
            } else {
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
        accountAdapter = AccountAdapter(accountList) { accountId ->
            Log.d("AccountSwitchingFragment", "Account clicked with ID: $accountId")

            // Переключаем аккаунт
            viewModel.switchToChildAccount(accountId)
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

            if (name.isBlank() || dateOfBirth.isBlank()) {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                val parentId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_parent_id"

                val newChildAccount = ChildAccount(name = name, dateOfBirth = dateOfBirth)

                viewModel.createChildAccount(parentId = parentId, child = newChildAccount) {
                    val newAccount = Account(name = newChildAccount.name, role = "Ребёнок", id = "someGeneratedId")
                    accountAdapter.addAccount(newAccount)

                    binding.etName.text.clear()
                    binding.etDateOfBirth.text.clear()

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





