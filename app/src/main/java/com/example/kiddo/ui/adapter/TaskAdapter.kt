package com.example.kiddo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kiddo.R
import com.example.kiddo.domain.model.Task

class TaskAdapter(
    private val onCompleteTask: (Task) -> Unit // Callback для завершения задачи
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = emptyList()

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitleTextView: TextView = view.findViewById(R.id.taskTitleTextView)
        val taskAssignedToTextView: TextView = view.findViewById(R.id.taskAssignedToTextView)
        val taskRewardTextView: TextView = view.findViewById(R.id.taskRewardTextView)
        val completeButton: Button = view.findViewById(R.id.taskCompleteButton) // Кнопка завершения
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitleTextView.text = task.title
        holder.taskAssignedToTextView.text = "Взял: ${task.assignedToName ?: "Не назначено"}"
        holder.taskRewardTextView.text = task.reward.toString()

        // Обработка нажатия на кнопку завершения
        holder.completeButton.setOnClickListener {
            onCompleteTask(task)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
