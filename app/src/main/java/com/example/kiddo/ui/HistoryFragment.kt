package com.example.kiddo.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kiddo.databinding.FragmentHistoryBinding
import com.example.kiddo.domain.model.Task
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.kiddo.presentation.TaskViewModel
import com.example.kiddo.ui.adapter.CompletedTaskAdapter


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModel()
    private lateinit var completedTaskAdapter: CompletedTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        completedTaskAdapter = CompletedTaskAdapter { task ->
            showRevertTaskDialog(task) // Вызываем функцию для отображения диалога
        }

        // Настраиваем RecyclerView
        binding.completedTasksRecyclerView.apply {
            adapter = completedTaskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Настраиваем SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Загружаем выполненные задачи заново
            viewModel.loadCompletedTasks()
        }

        observeViewModel()
        viewModel.loadCompletedTasks()
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let {
                completedTaskAdapter.submitList(it)
            }

            // Останавливаем анимацию обновления
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }

            // Останавливаем анимацию обновления
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    // Функция для отображения диалогового окна
    private fun showRevertTaskDialog(task: Task) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Отменить сданное дело?")
            .setMessage("Если не справились с заданием, дело вернется в список активных дел.")
            .setPositiveButton("Оставить") { dialogInterface, _ ->
                // Если пользователь нажал "Оставить", не делаем ничего
                dialogInterface.dismiss()
            }
            .setNegativeButton("Отменить") { dialogInterface, _ ->
                // Если пользователь нажал "Отменить", выполняем revertTaskStatus
                viewModel.revertTaskStatus(task)
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
