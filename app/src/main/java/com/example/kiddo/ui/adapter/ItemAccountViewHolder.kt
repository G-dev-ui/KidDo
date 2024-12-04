package com.example.kiddo.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kiddo.R

class ItemAccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val profileImage: ImageView = itemView.findViewById(R.id.item_profile_image)
    val name: TextView = itemView.findViewById(R.id.item_name)
    val role: TextView = itemView.findViewById(R.id.item_role)
}