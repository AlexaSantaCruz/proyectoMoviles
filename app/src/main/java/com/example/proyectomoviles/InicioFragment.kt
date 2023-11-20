package com.example.proyectomoviles

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class InicioFragment : Fragment() {
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

        names = getAllNames()

        names = loadNamesFromSharedPreferences()


        miRecyclerView = view.findViewById(R.id.recyclerView)
        miLayoutManager = LinearLayoutManager(activity)
        miAdapter = MyAdapter(names, R.layout.recycler_view_item, object : MyAdapter.OnItemClickListener {
            override fun onItemClick(name: String?, position: Int) {
                // Toast.makeText(activity, name + " - " + position, Toast.LENGTH_LONG).show()
                deleteName(position)
            }
        })

        miRecyclerView.layoutManager = miLayoutManager
        miRecyclerView.adapter = miAdapter

        setHasOptionsMenu(true)  // Indicar que el fragment tiene opciones de menú

        return view
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
            R.id.add_name -> {
                addName(0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addName(posicion: Int) {
        names.add(posicion, "New name" + (++counter))
        miAdapter.notifyItemInserted(posicion)
        miLayoutManager.scrollToPosition(posicion)
        saveNamesToSharedPreferences(names)
    }

    private fun deleteName(posicion: Int) {
        names.removeAt(posicion)
        miAdapter.notifyItemRemoved(posicion)
        saveNamesToSharedPreferences(names)
    }
}
