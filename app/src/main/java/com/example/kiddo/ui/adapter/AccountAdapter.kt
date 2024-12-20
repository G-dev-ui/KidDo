package com.example.kiddo.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kiddo.R

data class Account(val name: String, val role: String,val id: String)

class AccountAdapter(
    private val accountList: MutableList<Account>,
    private val onItemClick: (Account) -> Unit // Передаем ID ребенка
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
        holder.itemView.setOnClickListener {
            Log.d("AccountAdapter", "Item clicked: ${account.name}, ID: ${account.id}")
            // Передаем ID аккаунта, на который кликнули
            onItemClick(account) // Здесь передаем ID
        }
    }

    // Количество элементов в списке
    override fun getItemCount(): Int = accountList.size

    // Добавить элемент в список
    fun addAccount(account: Account) {
        accountList.add(account)
        notifyItemInserted(accountList.size - 1)
    }

    // Удалить элемент из списка
    fun clearAccounts() {
        accountList.clear()
        notifyDataSetChanged()
    }

    fun addAccounts(accounts: List<Account>) {
        Log.d("AccountAdapter", "Updating accounts: $accounts")
        accountList.clear()
        accountList.addAll(accounts)
        notifyDataSetChanged()
    }
}