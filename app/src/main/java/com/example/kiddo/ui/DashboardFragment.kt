package com.example.kiddo.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kiddo.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.kiddo.databinding.FragmentDashboardBinding
import com.example.kiddo.presentation.FamilyViewModel
import com.example.kiddo.ui.adapter.CategoryAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomNavigationView: BottomNavigationView

    private val familyViewModel: FamilyViewModel by viewModel()

    private var rewardCount = 0 // Переменная для хранения текущего счета награды

    private var selectedMemberId: String? = null // Переменная для хранения выбранного члена семьи

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // Настройка BottomSheet
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        val bottomSheet = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Устанавливаем высоту BottomSheet
        bottomNavigationView.viewTreeObserver.addOnPreDrawListener {
            val bottomNavHeight = bottomNavigationView.height
            bottomSheetBehavior.setPeekHeight(bottomNavHeight)
            true
        }

        // Настройка кнопки для открытия BottomSheet
        binding.addTaskButton.setOnClickListener {
            bottomSheet.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Настройка кнопок для изменения награды
        setupRewardButtons()

        // Настройка RecyclerView
        setupRecyclerView()

        // Наблюдаем за данными членов семьи
        observeFamilyMembers()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun observeFamilyMembers() {
        // Наблюдаем за изменением данных членов семьи
        familyViewModel.familyMembers.observe(viewLifecycleOwner) { members ->
            Log.d("DashboardFragment", "Family Members: $members") // Логирование для проверки данных
            val adapter = CategoryAdapter(members, ::onFamilyMemberSelected) // Передаем callback для обработки клика
            binding.categoryRecyclerView.adapter = adapter
        }

        // Вызов fetch для получения данных
        familyViewModel.fetchFamilyMembers() // Замените "yourFamilyId" на ID вашей семьи
    }

    private fun setupRewardButtons() {
        // Увеличение награды
        binding.rewardPlusButton.setOnClickListener {
            rewardCount++ // Увеличиваем счетчик
            updateRewardText()
        }

        // Уменьшение награды, не даем уйти в отрицательные значения
        binding.rewardMinusButton.setOnClickListener {
            if (rewardCount > 0) { // Проверяем, чтобы счетчик не стал отрицательным
                rewardCount-- // Уменьшаем счетчик
                updateRewardText()
            }
        }

        // Отображаем начальный счет в TextView
        updateRewardText()
    }

    private fun updateRewardText() {
        // Обновляем текст в TextView
        binding.rewardTextView.text = rewardCount.toString()
    }

    // Обработчик для выбора члена семьи
    private fun onFamilyMemberSelected(memberId: String) {
        selectedMemberId = memberId // Сохраняем выбранного члена семьи
        Log.d("DashboardFragment", "Selected member: $selectedMemberId")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





