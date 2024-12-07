package com.example.kiddo.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kiddo.ui.DashboardFragment
import com.example.kiddo.ui.HistoryFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // У нас два фрагмента

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DashboardFragment() // Первый экран
            1 -> HistoryFragment() // Второй экран
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}