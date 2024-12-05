package com.example.kiddo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kiddo.R
import com.example.kiddo.domain.model.Task

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = emptyList()

    // ViewHolder для элемента задачи
    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitleTextView: TextView = view.findViewById(R.id.taskTitleTextView)
        val taskAssignedToTextView: TextView = view.findViewById(R.id.taskAssignedToTextView)
        val taskRewardTextView: TextView = view.findViewById(R.id.taskRewardTextView)
        val starCoinIcon: ImageView = view.findViewById(R.id.starCoinIcon)
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
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged() // Уведомляем RecyclerView об изменениях
    }
}