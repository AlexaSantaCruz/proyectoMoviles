package com.example.proyectomoviles

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class DiarioSemanalDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("¿Quiere un recordatorio Semanal o Diario?")
            .setPositiveButton("Semanal") { dialogInterface, i ->
                mostrar_mensaje("Notificaciones Semanales");
            }
            .setNegativeButton("Diario") { dialogInterface, i ->
                mostrar_mensaje("Notificaciones diario");
            }
            .setNeutralButton("Solo en la fecha propuesta") { dialogInterface, i ->
                mostrar_mensaje("Notificacion en la fecha que has escogido");
            }
        return builder.create()
    }

    fun mostrar_mensaje(msj: String?) {
        val toast = Toast.makeText(activity, msj, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        toast.show()
    }
}
