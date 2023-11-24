package com.example.proyectomoviles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2

class PopupActivity : AppCompatActivity() {
    private lateinit var popupmenu:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_popup)
        popupmenu = findViewById(R.id.activity_main_popup)

        popupmenu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val menu = PopupMenu(this@PopupActivity, view)
                val inflater: MenuInflater = menu.getMenuInflater()
                inflater.inflate(R.menu.menu_add, menu.getMenu())

                menu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick (item:MenuItem):Boolean{
                        return when (item.getItemId()) {
                            R.id.registro -> {
                                // Cambia al tercer fragmento (Registro de Series)
                                val viewPager = findViewById<ViewPager2>(R.id.view_pager)
                                viewPager.currentItem = 2
                                true
                            }

                            R.id.buscador -> {
                                val viewPager = findViewById<ViewPager2>(R.id.view_pager)
                                viewPager.currentItem = 1
                                true
                            }

                            else -> false
                        }
                    }
                })
                menu.show()
            }
        })
    }

    fun mostrar_mensaje(msj: String):Boolean{
        val toast= Toast.makeText(this, msj, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        toast.show()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_add, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.buscador -> mostrar_mensaje("Puedes buscar tus series en aquí")
            R.id.registro -> mostrar_mensaje("Agrega los datos de tu serie")
            else -> super.onOptionsItemSelected(item)
        }
    }
}