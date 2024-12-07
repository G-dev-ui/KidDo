package com.example.kiddo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kiddo.R
import com.example.kiddo.domain.model.Task

class CompletedTaskAdapter(
    private val onReturnTaskClick: (Task) -> Unit // Обработчик клика на возврат задачи
) : RecyclerView.Adapter<CompletedTaskAdapter.CompletedTaskViewHolder>() {

    private var completedTasks: List<Task> = emptyList()

    fun submitList(tasks: List<Task>) {
        completedTasks = tasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_completed_task, parent, false)
        return CompletedTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompletedTaskViewHolder, position: Int) {
        val task = completedTasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = completedTasks.size

    inner class CompletedTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitleTextView: TextView = itemView.findViewById(R.id.taskTitleTextView)
        private val taskAssignedToTextView: TextView = itemView.findViewById(R.id.taskAssignedToTextView)
        private val taskRewardTextView: TextView = itemView.findViewById(R.id.taskRewardTextView)
        private val returnIcon: ImageView = itemView.findViewById(R.id.returnIcon) // Добавляем иконку возврата

        fun bind(task: Task) {
            taskTitleTextView.text = task.title
            taskAssignedToTextView.text = "Выполнил: ${task.assignedToName}"
            taskRewardTextView.text = task.reward.toString()

            // Обработчик клика на иконку возврата задачи
            returnIcon.setOnClickListener {
                onReturnTaskClick(task)
            }
        }
    }
}
