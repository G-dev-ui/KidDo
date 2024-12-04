package com.example.kiddo.ui.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kiddo.R

class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val userName: TextView = view.findViewById(R.id.userName)
    val userRole: TextView = view.findViewById(R.id.userRole)
}