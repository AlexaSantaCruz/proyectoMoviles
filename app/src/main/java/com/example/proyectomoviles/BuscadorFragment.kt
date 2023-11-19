package com.example.proyectomoviles

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

private val PREFS_NAME = "MyPrefs"
private val CHANNEL_ID = "series_notification_channel"
private lateinit var sharedPreferences: SharedPreferences
private lateinit var recomendacionesSerieRadioGroup: RadioGroup
private lateinit var autoCompleteTextView: AutoCompleteTextView
private lateinit var recibirNotificacionToggle: ToggleButton
private lateinit var finalizarCheckBox: CheckBox
private lateinit var genreSpinner: Spinner
private lateinit var tpdialog : Button
private lateinit var dpdialog : Button
private lateinit var adialog : Button



class BuscadorFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_buscador, container, false)

        autoCompleteTextView = view.findViewById(R.id.series)
        val imageView = view.findViewById<ImageView>(R.id.imagenSerie)

        val seriesArray = resources.getStringArray(R.array.series_array)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, seriesArray)
        autoCompleteTextView.setAdapter(adapter)

        finalizarCheckBox = view.findViewById(R.id.checkBoxFinalizar)
        genreSpinner = view.findViewById(R.id.genreSerie)

        adialog = view.findViewById(R.id.activity_main_adialog)
        adialog.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?) {
                val fragment: DialogFragment = DiarioSemanalDialog()
                fragment.show(childFragmentManager, "Recordatorios")
            }
        })

        tpdialog = view.findViewById(R.id.activity_main_tpdialog)
        tpdialog.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val fragment: DialogFragment = HoraDialog()
                fragment.show(childFragmentManager, "Hora")
            }
        })

        dpdialog = view.findViewById(R.id.activity_main_dpdialog)
        dpdialog.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?) {
                val fragment: DialogFragment = FechaDialog()
                fragment.show(childFragmentManager, "Calendario")
            }
        })




        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedSerie = adapter.getItem(position).toString()
            val imageName = selectedSerie.toLowerCase().replace(" ", "")
            val imageResourceId = resources.getIdentifier(imageName, "drawable", requireContext().packageName)
            imageView.setImageResource(imageResourceId)

            recibirNotificacionToggle = view.findViewById(R.id.recibirNotificacion)
            recomendacionesSerieRadioGroup = view.findViewById(R.id.recomendation)

            sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            cargarEstado(autoCompleteTextView)

            val guardarButton = view.findViewById<Button>(R.id.guardar)
            guardarButton.setOnClickListener {
                save(autoCompleteTextView)
                if (recibirNotificacionToggle.isChecked) {
                    enviarNotificacion(autoCompleteTextView)
                }
            }
        }
        return view
    }

    private fun enviarNotificacion(autoCompleteTextView: AutoCompleteTextView) {
        // Obtiene el nombre de la serie actual del AutoCompleteTextView
        val nombreSerieActual = autoCompleteTextView.text.toString()


        if (NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {

            // Crea y muestra una notificación
            createNotificationChannel(nombreSerieActual)


            Log.d("Notification", "Sending notification for $nombreSerieActual")


            val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Te suscribiste a una serie")
                .setContentText("Te has suscrito a $nombreSerieActual")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(requireContext())) {
                // Use a unique notification ID (e.g., based on current time)
                val notificationId = System.currentTimeMillis().toInt()
                notify(notificationId, builder.build())
            }
        }
        else{
            Toast.makeText(
                requireContext(),
                "Notificaciones desabilitadas, por favor habilitalas en configuracion",
                Toast.LENGTH_LONG
            ).show()

        }

    }


    private fun createNotificationChannel(nombreSerie: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = nombreSerie
            val descriptionText = "Notificaciones para la serie: $nombreSerie"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun save(autoCompleteTextView: AutoCompleteTextView) {
        val editor = sharedPreferences.edit()

        // Obtiene el nombre de la serie actual del AutoCompleteTextView
        val nombreSerieActual = autoCompleteTextView.text.toString()

        // Guarda el estado del ToggleButton con el nombre de la serie como parte de la clave
        editor.putBoolean("$nombreSerieActual.recibirNotificacion", recibirNotificacionToggle.isChecked)

        // Guarda el ID del RadioButton seleccionado con el nombre de la serie como parte de la clave
        val radioButtonId = recomendacionesSerieRadioGroup.checkedRadioButtonId
        editor.putInt("$nombreSerieActual.recomendacionesID", radioButtonId)

        // Guarda el estado del CheckBox con el nombre de la serie como parte de la clave
        editor.putBoolean("$nombreSerieActual.finalizar", finalizarCheckBox.isChecked)

        // Guarda el índice del elemento seleccionado en el Spinner
        val selectedGenreIndex = genreSpinner.selectedItemPosition
        editor.putInt("$nombreSerieActual.genreIndex", selectedGenreIndex)

        // Aplica los cambios
        editor.apply()
    }


    private fun cargarEstado(autoCompleteTextView: AutoCompleteTextView) {
        // Obtiene el nombre de la serie actual del AutoCompleteTextView
        val nombreSerieActual = autoCompleteTextView.text.toString()

        // Carga el estado del ToggleButton con el nombre de la serie como parte de la clave
        val recibirNotificacion =
            sharedPreferences.getBoolean("$nombreSerieActual.recibirNotificacion", false)
        recibirNotificacionToggle.isChecked = recibirNotificacion

        // Carga el ID del RadioButton seleccionado con el nombre de la serie como parte de la clave
        val radioButtonId =
            sharedPreferences.getInt("$nombreSerieActual.recomendacionesID", -1)

        // Si no hay un RadioButton seleccionado previamente, radioButtonId será -1
        if (radioButtonId != -1) {
            recomendacionesSerieRadioGroup.check(radioButtonId)
        } else {
            // Si no hay datos guardados previamente, deja el estado sin marcar
            recomendacionesSerieRadioGroup.clearCheck()
        }

        // Carga el estado del CheckBox con el nombre de la serie como parte de la clave
        val finalizar = sharedPreferences.getBoolean("$nombreSerieActual.finalizar", false)
        finalizarCheckBox.isChecked = finalizar

        // Carga el índice del elemento seleccionado en el Spinner
        val genreIndex = sharedPreferences.getInt("$nombreSerieActual.genreIndex", -1)
        if (genreIndex != -1) {
            genreSpinner.setSelection(genreIndex)
        }
    }


}
