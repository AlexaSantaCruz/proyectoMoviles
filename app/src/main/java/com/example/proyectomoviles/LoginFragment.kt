package com.example.proyectomoviles

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2

class SharedViewModel : ViewModel() {
    var userData: UserData? = null
}
data class UserData(
    val selectedImageUri: Uri?,
    val nombreSerie: String,
    val finalizar: Boolean,
    val recomendacionID: Int,
    val genreIndex: Int,
    val recibirNotificaciones: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(selectedImageUri, flags)
        parcel.writeString(nombreSerie)
        parcel.writeByte(if (finalizar) 1 else 0)
        parcel.writeInt(recomendacionID)
        parcel.writeInt(genreIndex)
        parcel.writeByte(if (recibirNotificaciones) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}

class LoginFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()


    private lateinit var ingresarFotoButton: Button
    private lateinit var guardarFotoImageView: ImageView
    private lateinit var nombreSerieEditText: EditText
    private lateinit var checkBoxFinalizar: CheckBox
    private lateinit var recomendacionRadioGroup: RadioGroup
    private lateinit var genreSpinner: Spinner
    private lateinit var recibirNotificacionesToggle: ToggleButton
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedImageUri: Uri? = null


    private val PREFS_NAME = "MyPrefs"

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Imagen seleccionada, obtén la URI
                val data: Intent? = result.data
                selectedImageUri = data?.data
                guardarFotoImageView.setImageURI(selectedImageUri)

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Inicializar las vistas aquí
        ingresarFotoButton = view.findViewById(R.id.ingresarFoto)
        guardarFotoImageView = view.findViewById(R.id.guardarFoto)
        nombreSerieEditText = view.findViewById(R.id.nombreSerieEditText)
        checkBoxFinalizar = view.findViewById(R.id.checkBoxAccept)
        recomendacionRadioGroup = view.findViewById(R.id.recomendation)
        genreSpinner = view.findViewById(R.id.genreSerie)
        recibirNotificacionesToggle = view.findViewById(R.id.notifications)

        // Configurar el clic del botón
        ingresarFotoButton.setOnClickListener {
            openGallery()
        }

        // En tu onCreateView
        val guardarButton: Button = view.findViewById(R.id.guardar)
        guardarButton.setOnClickListener {
            saveData(view)
        }

        // Inicializar SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)



        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    fun saveData(view: View) {
        val editor = sharedPreferences.edit()

        // Guarda la URI de la imagen
        editor.putString("selectedImageUri", selectedImageUri.toString())

        // Guarda el nombre de la serie
        editor.putString("nombreSerie", nombreSerieEditText.text.toString())

        // Guarda el estado del CheckBox
        editor.putBoolean("finalizar", checkBoxFinalizar.isChecked)

        // Guarda el ID del RadioButton seleccionado
        val radioButtonId = recomendacionRadioGroup.checkedRadioButtonId
        editor.putInt("recomendacionID", radioButtonId)

        // Guarda el índice del elemento seleccionado en el Spinner
        val selectedGenreIndex = genreSpinner.selectedItemPosition
        editor.putInt("genreIndex", selectedGenreIndex)

        // Guarda el estado del ToggleButton
        editor.putBoolean("recibirNotificaciones", recibirNotificacionesToggle.isChecked)

        // Aplica los cambios
        editor.apply()

        Toast.makeText(requireContext(), "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()

        // Log para verificar en la consola
        Log.d("LoginFragment", "Datos guardados:\n" +
                "selectedImageUri: $selectedImageUri\n" +
                "nombreSerie: ${nombreSerieEditText.text}\n" +
                "finalizar: ${checkBoxFinalizar.isChecked}\n" +
                "recomendacionID: $radioButtonId\n" +
                "genreIndex: $selectedGenreIndex\n" +
                "recibirNotificaciones: ${recibirNotificacionesToggle.isChecked}")

        val userData = UserData(
            selectedImageUri,
            nombreSerieEditText.text.toString(),
            checkBoxFinalizar.isChecked,
            recomendacionRadioGroup.checkedRadioButtonId,
            genreSpinner.selectedItemPosition,
            recibirNotificacionesToggle.isChecked
        )

        sharedViewModel.userData = userData
        parentFragmentManager.setFragmentResult("requestKey", bundleOf("userData" to userData))

                    // Cambia al tercer fragmento (Registro de Series)
                    val viewPager = (requireActivity() as MainActivity).findViewById<ViewPager2>(R.id.view_pager)
                    viewPager.currentItem = 0



    }

}
