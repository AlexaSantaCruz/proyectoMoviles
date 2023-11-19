package com.example.proyectomoviles

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class FechaDialog : DialogFragment() {

    class HoraDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c: Calendar = Calendar.getInstance()
            val year: Int = c.get(Calendar.YEAR)
            val month: Int = c.get(Calendar.MONTH)
            val day: Int = c.get(Calendar.DAY_OF_MONTH)
            val dpd =
                DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year,
                                                                                         monthOfYear, dayOfMonth ->
                    val toast: Toast = Toast.makeText(
                        activity,
                        dayOfMonth.toString() + " " + (monthOfYear + 1) + " " + year,
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
                    toast.show()
                }, year, month, day)
            dpd.show()
            return dpd
        }
    }
}