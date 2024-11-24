package com.example.kiddo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kiddo.R
import com.example.kiddo.databinding.FragmentAccountSwitchingBinding
import com.example.kiddo.databinding.FragmentRegistrationBinding


class Account_SwitchingFragment : Fragment() {

    private lateinit var accountAdapter: AccountAdapter

    // Используем ViewBinding для связывания с элементами макета
    private var _binding: FragmentAccountSwitchingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountSwitchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация адаптера с начальными данными
        accountAdapter = AccountAdapter(mutableListOf(
            Account("Иван", "Родитель"),
            Account("Мария", "Ребенок")
        ))

        // Настройка RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context) // Линейный список
            adapter = accountAdapter
        }

        // Обработчик кнопки "Добавить аккаунт ребенка"
        binding.btnAddAccount.setOnClickListener {
            // Добавить новый аккаунт (для примера создаем статичный)
            val newAccount = Account("Новый пользователь", "Ребенок")
            accountAdapter.addAccount(newAccount)

            // Пример: можно показать сообщение о добавлении
            Toast.makeText(context, "Аккаунт добавлен", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
