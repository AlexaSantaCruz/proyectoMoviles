package com.example.proyectomoviles

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter constructor(
    var series: List<String>,
    var layout: Int,
    var itemListener: OnItemClickListener


) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var favorites: MutableList<String> = mutableListOf()




    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.guardarFoto)
        var textViewName: TextView = itemView.findViewById(R.id.textViewName)

        fun bind(serie: String, itemListener: OnItemClickListener, isLastItem: Boolean, favorites: MutableList<String>) {
            val imageName = serie.toLowerCase().replace(" ", "")
            val imageResourceId =
                itemView.resources.getIdentifier(
                    imageName,
                    "drawable",
                    itemView.context.packageName
                )

            imageView.setImageResource(imageResourceId)
            textViewName.text = serie

            itemView.setOnClickListener {
                itemListener.onItemClick(serie, adapterPosition)
            }

            // Añadir margen inferior solo al último elemento
            val params = itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = if (isLastItem) 5 else 5
            itemView.layoutParams = params

            val isFavorite = favorites.contains(serie)
            // Muestra el estado de favoritos, por ejemplo, cambiando el color de fondo
            itemView.setBackgroundColor(if (isFavorite) Color.YELLOW else Color.WHITE)


        }
    }

    fun updateFavorites(newFavorites: List<String>) {
        favorites.clear()
        favorites.addAll(newFavorites)
        notifyDataSetChanged()
        Log.d("MyAdapter", "updateFavorites called. Favorites: $favorites")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return series.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isLastItem = position == itemCount - 1
        holder.bind(series[position], itemListener, isLastItem, favorites)
    }

    interface OnItemClickListener {
        fun onItemClick(name: String?, position: Int)

    }
}
