package com.example.kiddo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kiddo.R

data class Account(val name: String, val role: String)

class AccountAdapter(
    private val accountList: MutableList<Account>
) : RecyclerView.Adapter<ItemAccountViewHolder>() {

    // Создание ViewHolder из XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAccountViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return ItemAccountViewHolder(itemView)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: ItemAccountViewHolder, position: Int) {
        val account = accountList[position]
        holder.name.text = account.name
        holder.role.text = account.role
        // Пример, если нужно установить изображение:
        holder.profileImage.setImageResource(R.drawable.baseline_people_24)
    }

    // Количество элементов в списке
    override fun getItemCount(): Int = accountList.size

    // Добавить элемент в список
    fun addAccount(account: Account) {
        accountList.add(account)
        notifyItemInserted(accountList.size - 1)
    }

    // Удалить элемент из списка
    fun removeAccount(position: Int) {
        if (position in accountList.indices) {
            accountList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addAccounts(accounts: List<Account>) {
        accountList.clear() // Очищаем старый список (если нужно)
        accountList.addAll(accounts)
        notifyDataSetChanged() // Обновляем адаптер
    }
}