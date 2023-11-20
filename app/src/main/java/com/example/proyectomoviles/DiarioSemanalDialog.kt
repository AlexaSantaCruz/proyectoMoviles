package com.example.proyectomoviles

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class DiarioSemanalDialog : DialogFragment() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("¿Quiere un recordatorio Semanal, Diario o solo en la fecha propuesta?")
            .setPositiveButton("Semanal") { _, _ ->
                sharedPreferences.edit().putString("diarioSemanal", "semanal").apply()
                mostrar_mensaje("Notificaciones Semanales")
            }
            .setNegativeButton("Diario") { _, _ ->
                sharedPreferences.edit().putString("diarioSemanal", "diario").apply()
                mostrar_mensaje("Notificaciones diarias")
            }
            .setNeutralButton("Solo en la fecha propuesta") { _, _ ->
                sharedPreferences.edit().putString("diarioSemanal", "unico").apply()
                mostrar_mensaje("Notificación en la fecha que has escogido")
            }
        return builder.create()
    }

    // Función para mostrar un mensaje
    fun mostrar_mensaje(msj: String?) {
        val toast = Toast.makeText(activity, msj, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        toast.show()
    }
}

