package com.example.turisteo.BD.Entities

import androidx.room.*
import com.example.turisteo.Common.Constants

@Entity(tableName = Constants.PLACES)
data class Place (
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "latitude") var latitude: Double,
    @ColumnInfo(name = "longitude") var longitude: Double
){
    override fun toString(): String {
        return this.name
    }
}