package com.example.kiddo.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kiddo.R
import com.example.kiddo.domain.model.User

class CategoryAdapter(
    private val users: List<User>,
    private val onItemClick: (String) -> Unit // Callback для обработки клика
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private var selectedPosition = -1 // Переменная для отслеживания выбранного элемента

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val user = users[position]
        holder.userName.text = user.name
        holder.userRole.text = user.role

        // Устанавливаем фоновый цвет для подсветки выбранного элемента
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#00FF00")) // Зеленый цвет для подсветки
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT) // Без подсветки
        }

        // Обработчик нажатия на элемент
        holder.itemView.setOnClickListener {
            // Обновляем выбранную позицию
            selectedPosition = holder.adapterPosition
            onItemClick(user.id) // Вызываем callback с id выбранного члена семьи
            notifyDataSetChanged() // Перерисовываем весь список, чтобы обновить подсветку
        }
    }

    override fun getItemCount(): Int = users.size
}


