package com.example.proyectomoviles

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


private val PREFS_NAME = "MyPrefs"
private val CHANNEL_ID = "series_notification_channel"
lateinit var sharedPreferences: SharedPreferences
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

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


        val adialog = view.findViewById<Button>(R.id.activity_main_adialog)
        adialog.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Cambia el tipo de fragment a DiarioSemanalDialog
                val fragment = DiarioSemanalDialog()
                // Pasa la instancia de SharedPreferences al diálogo
                fragment.sharedPreferences = sharedPreferences
                fragment.show(childFragmentManager, "Recordatorios")
            }
        })


        val tpdialog = view.findViewById<Button>(R.id.activity_main_tpdialog)
        tpdialog.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val fragment = HoraDialog()
                // Pasa la instancia de SharedPreferences al diálogo
                fragment.sharedPreferences = sharedPreferences
                fragment.show(childFragmentManager, "Hora")
            }
        })

        val dpdialog = view.findViewById<Button>(R.id.activity_main_dpdialog)
        dpdialog.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val fragment = FechaDialog()
                // Pasa la instancia de SharedPreferences al diálogo
                fragment.sharedPreferences = sharedPreferences
                fragment.show(childFragmentManager, "Calendario")
            }
        })

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedSerie = adapter.getItem(position).toString()
            val imageName = selectedSerie.toLowerCase().replace(" ", "")
            val imageResourceId =
                resources.getIdentifier(imageName, "drawable", requireContext().packageName)
            imageView.setImageResource(imageResourceId)

            recibirNotificacionToggle = view.findViewById(R.id.recibirNotificacion)
            recomendacionesSerieRadioGroup = view.findViewById(R.id.recomendation)

            sharedPreferences =
                requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            cargarEstado(autoCompleteTextView)

            val guardarButton = view.findViewById<Button>(R.id.guardar)
            guardarButton.setOnClickListener {
                save(autoCompleteTextView)

                // Verifica si se activó el toggle de recibir notificaciones
                if (recibirNotificacionToggle.isChecked) {


                    // Obtiene la información necesaria para la notificación
                    val nombreSerieActual = autoCompleteTextView.text.toString()
                    val horaSeleccionada = sharedPreferences.getString("selectedHour", null)
                    val fechaSeleccionada = sharedPreferences.getString("selectedDate", null)
                    val recordatorioTipo = sharedPreferences.getString("diarioSemanal", null)

                    enviarNotificacion(autoCompleteTextView, horaSeleccionada, fechaSeleccionada, recordatorioTipo)

                    // Verifica si se ha seleccionado una hora y fecha
                    if (!horaSeleccionada.isNullOrEmpty() && !fechaSeleccionada.isNullOrEmpty()) {
                        // Programa la notificación según el tipo de recordatorio
                        scheduleNotification(
                            nombreSerieActual,
                            horaSeleccionada,
                            fechaSeleccionada,
                            recordatorioTipo
                        )
                    } else {
                        // Muestra un mensaje de error si no se ha seleccionado hora o fecha
                        Toast.makeText(
                            requireContext(),
                            "Selecciona una hora y una fecha para recibir notificaciones",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
            return view
        }

    private fun enviarNotificacion(
        autoCompleteTextView: AutoCompleteTextView,
        horaSeleccionada: String?,
        fechaSeleccionada: String?,
        recordatorioTipo: String?) {


        // Obtiene el nombre de la serie actual del AutoCompleteTextView
        val nombreSerieActual = autoCompleteTextView.text.toString()


        if (NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {

            // Crea y muestra una notificación
            createNotificationChannel(nombreSerieActual)


            Log.d("Notification", "Sending notification for $nombreSerieActual")



            val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Te suscribiste a una serie")
                .setContentText("Te has suscrito a $nombreSerieActual se te enviaran notificaciones a las $horaSeleccionada, fecha $fechaSeleccionada, con la frecuencia de $recordatorioTipo")
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
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                notificationManager.createNotificationChannel(channel)
                Log.d("Notification", "Creating notification channel for $nombreSerie")

            }
        }
    }

    private fun scheduleNotification(
        nombreSerie: String,
        horaSeleccionada: String,
        fechaSeleccionada: String,
        recordatorioTipo: String?
    ) {
        // Convierte la fecha y la hora seleccionada a milisegundos
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd MM yyyy HH:mm", Locale.getDefault())
        val date = formatter.parse("$fechaSeleccionada $horaSeleccionada")
        date?.let {
            calendar.time = it
        }

        // Crea un intent para la notificación
        val notificationIntent = Intent(requireContext(), NotificationReceiver::class.java)
        notificationIntent.putExtra("nombreSerie", nombreSerie)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or  FLAG_IMMUTABLE
        )

        // Obtiene el servicio de alarma
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Programa la notificación según el tipo de recordatorio
        when (recordatorioTipo) {
            "diario" -> {
                // Programa una notificación diaria
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
            "semanal" -> {
                // Programa una notificación semanal
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
                )
            }
            "unico" -> {
                // Programa una notificación única en la fecha y hora seleccionadas
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        }

        // Muestra un mensaje de éxito
        Toast.makeText(
            requireContext(),
            "Notificación programada para $nombreSerie",
            Toast.LENGTH_SHORT
        ).show()
    }
}


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val nombreSerie = intent.getStringExtra("nombreSerie")
            showNotification(context, nombreSerie)
        }
    }

    private fun showNotification(context: Context, nombreSerie: String?) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Te suscribiste a una serie")
            .setContentText("Te has suscrito a $nombreSerie")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            // Use a unique notification ID (e.g., based on current time)
            val notificationId = System.currentTimeMillis().toInt()
            notify(notificationId, builder.build())
        }
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



