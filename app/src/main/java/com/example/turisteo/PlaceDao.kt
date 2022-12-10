package com.example.turisteo

import androidx.room.*
import com.example.turisteo.BD.Entities.Place

@Dao
interface PlaceDao {
    @Query("SELECT id, name, description, latitude, longitude " +
            "FROM Places ORDER BY name;")
    fun getPlaces(): List<Place>

    @Insert
    fun insertPlace(place: Place): Long

    @Delete
    fun removePlace(place: Place) : Long
}