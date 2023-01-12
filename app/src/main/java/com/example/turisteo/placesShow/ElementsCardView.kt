package com.example.turisteo.placesShow

import android.view.View

import android.widget.TextView

import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView.ViewHolder

import com.example.turisteo.BD.AppDatabase

import com.example.turisteo.BD.Entities.Place

import com.example.turisteo.MainActivity

import com.example.turisteo.R

class ElementsCardView(
    itemView: View, private val database: AppDatabase,
    private val recyclerViewAdapter: RecyclerViewAdapter
) : ViewHolder(itemView) {
    private var nameTv: TextView

    init {
        //localize each cardView components
        nameTv = itemView.findViewById(R.id.cardview_tvNombreLugar)

        itemView.setOnClickListener { v ->
            createPlaceAlertDialog(itemView.context as MainActivity, v.tag as Place).show()
        }
        itemView.setOnLongClickListener { v ->
            createPlaceAlertDialog(itemView.context as MainActivity, v.tag as Place).show()
            false
        }
    }

    private fun createPlaceAlertDialog(activity: MainActivity, place: Place): AlertDialog {
        return AlertDialog.Builder(activity)
            .setTitle(R.string.eliminar_lugar)
            .setMessage(R.string.mensaje_eliminar_lugar)
            .setCancelable(false)
            .setNegativeButton(
                R.string.cancelar
            ) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(
                R.string.aceptar
            ) { _, _ ->
                database.PlaceDao().removePlace(place)
                recyclerViewAdapter.removeItem(adapterPosition)
            }
            .create()
    }

    fun toTvName(nameTv: String) {
        this.nameTv.text = nameTv
    }
}