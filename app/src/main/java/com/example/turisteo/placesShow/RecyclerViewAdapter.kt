package com.example.turisteo.placesShow

import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import androidx.recyclerview.widget.RecyclerView.ViewHolder

import com.example.turisteo.BD.AppDatabase

import com.example.turisteo.BD.Entities.Place

import com.example.turisteo.R

class RecyclerViewAdapter(private val places: ArrayList<Place>, private val database: AppDatabase) :
    RecyclerView.Adapter<ViewHolder>() {
    private var selectedPlaceAdapter: Place? = null
    private var elementsCardView: ElementsCardView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //create layout inflater depends from parent's context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cardview_recyclerview, parent, false)
        //create cardView an pass recyclerView's view, and db
        return ElementsCardView(view, database, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chargedPlace = places[position]
        val myViewHolder = holder as ElementsCardView
        elementsCardView = myViewHolder
        val view = holder.itemView
        //put tag in the View respects its position in the adapter
        view.tag = chargedPlace
        selectedPlaceAdapter = chargedPlace
        myViewHolder.toTvName(chargedPlace.name)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    fun removeItem(position: Int) {
        places.removeAt(position)
        notifyItemRemoved(position)
    }

}