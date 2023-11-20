package com.example.proyectomoviles

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class FechaDialog : DialogFragment() {

    // Variable para almacenar preferencias compartidas
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)

        // Inicializar preferencias compartidas

        // Crear un DatePickerDialog
        val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // Mostrar la fecha seleccionada en un Toast
            val toast: Toast = Toast.makeText(
                activity,
                "$dayOfMonth ${monthOfYear + 1} $year",
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
            toast.show()

            // Almacenar la fecha seleccionada en SharedPreferences
            saveSelectedDate(dayOfMonth, monthOfYear + 1, year)
        }, year, month, day)

        // Mostrar el DatePickerDialog
        dpd.show()
        return dpd
    }

    // Función para guardar la fecha seleccionada en SharedPreferences
    fun saveSelectedDate(day: Int, month: Int, year: Int) {
        val editor = sharedPreferences.edit()

        // Guarda la fecha en formato String con la clave adecuada
        editor.putString("selectedDate", "$day $month $year")

        // Aplica los cambios
        editor.apply()
    }

}

