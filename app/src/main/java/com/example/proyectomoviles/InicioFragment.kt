package com.example.proyectomoviles

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2


class InicioFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()


    private lateinit var names: MutableList<String>
    private lateinit var miRecyclerView: RecyclerView
    private lateinit var miAdapter: MyAdapter
    private lateinit var miLayoutManager: RecyclerView.LayoutManager
    private var counter = 0
    private val PREFS_NAME = "MyPrefs"
    private val SERIES_KEY = "seriesKey"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        names = loadNamesFromSharedPreferences()
        names = getAllNames()

        miRecyclerView = view.findViewById(R.id.recyclerView)


        miLayoutManager = LinearLayoutManager(activity)
        miAdapter = MyAdapter(names, R.layout.recycler_view_item, object : MyAdapter.OnItemClickListener {
            override fun onItemClick(name: String?, position: Int) {
                // Toast.makeText(activity, name + " - " + position, Toast.LENGTH_LONG).show()

                showContextMenu(position)
            }

        })

        miRecyclerView.layoutManager = miLayoutManager
        miRecyclerView.adapter = miAdapter

        setHasOptionsMenu(true)  // Indicar que el fragment tiene opciones de menú

        parentFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->
            val receivedUserData = bundle.getParcelable<UserData>("userData")

            receivedUserData?.let {
                names.add(it.nombreSerie)


                miAdapter.notifyItemInserted(names.size - 1)
            }
        }



        return view
    }

    private fun showContextMenu(position: Int) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView?.let {
            val viewHolder = it.findViewHolderForAdapterPosition(position)
            val view = viewHolder?.itemView

            view?.let {
                val popupMenu = PopupMenu(requireContext(), it)
                popupMenu.menuInflater.inflate(R.menu.context_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_option_1 -> {
                            agregarFavoritos(position)
                            true
                        }
                        R.id.menu_option_2 -> {
                            // Acción para la opción 2
                            deleteName(position)
                            true
                        }
                        else -> false
                    }
                }

                popupMenu.show()
            }
        }
    }



    private fun agregarFavoritos(position: Int) {
        val selectedPosition = position
        if (selectedPosition != RecyclerView.NO_POSITION) {
            val selectedSerie = names[selectedPosition]

            // If already a favorite, remove it; otherwise, add it
            if (miAdapter.favorites.contains(selectedSerie)) {
                miAdapter.favorites.remove(selectedSerie)
            } else {
                miAdapter.favorites.add(selectedSerie)
                Log.d("MyAdapter", "series: ${miAdapter.favorites}")

            }


            // Notify the adapter that the data set has changed
            miAdapter.notifyItemChanged(selectedPosition)

            // Add a log statement or Toast message to check if this function is being called
            Toast.makeText(requireContext(), "Favorites updated for position $selectedPosition", Toast.LENGTH_SHORT).show()

        }


    }




    private fun loadNamesFromSharedPreferences(): MutableList<String> {
        val sharedPreferences = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences?.getStringSet(SERIES_KEY, HashSet<String>())?.toMutableList() ?: mutableListOf()
    }

    private fun saveNamesToSharedPreferences(names: MutableList<String>) {
        val sharedPreferences = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putStringSet(SERIES_KEY, HashSet(names))
        editor?.apply()
    }

    private fun getAllNames(): MutableList<String> {
        return resources.getStringArray(R.array.series_array).toMutableList()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.registro -> {
                // Cambia al tercer fragmento (Registro de Series)
                val viewPager = (requireActivity() as MainActivity).findViewById<ViewPager2>(R.id.view_pager)
                viewPager.currentItem = 2
                true
            }
            R.id.buscar -> {
                val viewPager = (requireActivity() as MainActivity).findViewById<ViewPager2>(R.id.view_pager)
                viewPager.currentItem = 1
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarDatos() {
        Toast.makeText(activity,   " - " , Toast.LENGTH_LONG).show()

    }

    private fun deleteName(posicion: Int) {
        names.removeAt(posicion)
        miAdapter.notifyItemRemoved(posicion)
        saveNamesToSharedPreferences(names)
    }
}
