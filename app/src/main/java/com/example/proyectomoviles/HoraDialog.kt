package com.example.proyectomoviles

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar


class HoraDialog : DialogFragment() {
    lateinit var sharedPreferences: SharedPreferences

    // Variable para almacenar la hora seleccionada
    var horaSeleccionada: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val hour: Int = c.get(Calendar.HOUR_OF_DAY)
        val minute: Int = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(requireActivity(), TimePickerDialog.OnTimeSetListener { view, h, m ->
            // Almacena la hora seleccionada en la variable
            horaSeleccionada = "$h:$m"

            saveSelectedTime(h, m)

            // Muestra un mensaje con la hora seleccionada
            val toast: Toast = Toast.makeText(activity, "Hora seleccionada: $horaSeleccionada", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
            toast.show()
        }, hour, minute, false)

        tpd.show()
        return tpd
    }

    fun saveSelectedTime(hour: Int, minute: Int) {
        val editor = sharedPreferences.edit()
        editor.putString("selectedHour", "$hour:$minute")
        editor.apply()
    }
}
