package com.example.proyectomoviles

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPageAdapter(activity: FragmentActivity, private val fragmentManager: FragmentManager) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InicioFragment()
            1 -> BuscadorFragment()
            2 -> LoginFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Inicio"
            1 -> "Buscador"
            2 -> "Registro de Series"
            else -> null
        }
    }


}
