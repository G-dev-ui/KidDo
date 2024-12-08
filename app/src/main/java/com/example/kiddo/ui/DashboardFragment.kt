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
import com.example.kiddo.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.kiddo.databinding.FragmentDashboardBinding
import com.example.kiddo.domain.model.Task
import com.example.kiddo.presentation.FamilyViewModel
import com.example.kiddo.presentation.TaskViewModel
import com.example.kiddo.ui.adapter.CategoryAdapter
import com.example.kiddo.ui.adapter.TaskAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.UUID


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomNavigationView: BottomNavigationView

    private val familyViewModel: FamilyViewModel by viewModel()
    private val taskViewModel: TaskViewModel by viewModel()

    private lateinit var taskAdapter: TaskAdapter // Адаптер для задач

    private var rewardCount = 0 // Переменная для хранения текущего счета награды

    private var selectedMemberId: String? = null // Переменная для хранения выбранного члена семьи



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.createTaskButton.setOnClickListener {
            createTask()
        }

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

        // Наблюдаем за задачами
        observeTasks()

        observeUserRole()

        // Настройка SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Загружаем данные заново
            taskViewModel.loadTasks()
        }

        return binding.root
    }


    private fun createTask() {
        val taskName = binding.taskNameEditText.text.toString().trim()
        if (taskName.isEmpty()) {
            Toast.makeText(requireContext(), "Введите название задачи", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedMemberId == null) {
            Toast.makeText(requireContext(), "Выберите члена семьи", Toast.LENGTH_SHORT).show()
            return
        }

        // Найти имя члена семьи по ID
        val assignedMember = familyViewModel.familyMembers.value?.find { it.id == selectedMemberId }
        val assignedToName = assignedMember?.name ?: "Неизвестный"

        // Создаем объект задачи
        val task = Task(
            id = UUID.randomUUID().toString(), // Уникальный идентификатор
            title = taskName,
            assignedToId = selectedMemberId, // Устанавливаем ID назначенного члена семьи
            assignedToName = assignedToName,
            reward = rewardCount
        )

        taskViewModel.createTask(task)

        // Сброс данных после создания задачи
        resetTaskForm()

        // Закрываем BottomSheet
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun resetTaskForm() {
        binding.taskNameEditText.text.clear()
        rewardCount = 0
        updateRewardText()
        selectedMemberId = null
        binding.categoryRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        binding.categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Настроим RecyclerView для задач
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = TaskAdapter { task ->
            completeTask(task) // Вызываем метод завершения задачи
        } // Инициализируем адаптер для задач
        binding.tasksRecyclerView.adapter = taskAdapter
    }

    private fun observeFamilyMembers() {
        // Наблюдаем за изменением данных членов семьи
        familyViewModel.familyMembers.observe(viewLifecycleOwner) { members ->
            Log.d(
                "DashboardFragment",
                "Family Members: $members"
            ) // Логирование для проверки данных
            val adapter = CategoryAdapter(
                members,
                ::onFamilyMemberSelected
            ) // Передаем callback для обработки клика
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


    private fun observeTasks() {
        // Подгрузить задачи сразу при старте
        taskViewModel.loadTasks()

        // Наблюдаем за задачами
        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let {
                taskAdapter.updateTasks(it) // Обновляем список задач в адаптере
                binding.swipeRefreshLayout.isRefreshing = false // Останавливаем индикатор обновления
            }
        }

        taskViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("DashboardFragment", "Ошибка при обработке задач: $it")
                Toast.makeText(requireContext(), "Ошибка: $it", Toast.LENGTH_SHORT).show()
                binding.swipeRefreshLayout.isRefreshing = false // Останавливаем индикатор обновления
            }
        }
    }

    private fun observeUserRole() {
        familyViewModel.userRole.observe(viewLifecycleOwner) { role ->
            // Показываем кнопку только если роль "Parent" или другая подходящая
            binding.addTaskButton.visibility = if (role == "Родитель") {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        // Запускаем получение роли
        familyViewModel.fetchUserRole()
    }

    private fun completeTask(task: Task) {
        taskViewModel.completeTask(task) // Передаем задачу в ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





