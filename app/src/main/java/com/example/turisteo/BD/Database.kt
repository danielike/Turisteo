package com.example.turisteo.BD

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.turisteo.BD.Entities.Place
import com.example.turisteo.Common.Constants
import com.example.turisteo.PlaceDao

@Database(entities = [Place::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
   abstract fun PlaceDao(): PlaceDao

   companion object {
       private var INSTANCE: AppDatabase? = null

       fun getInstance(context: Context): AppDatabase =
           INSTANCE ?: synchronized(this) {
               INSTANCE ?: buildDatabase(context).also {
                   INSTANCE = it
               }
           }

       @JvmStatic
       fun destroyInstance() {
           if (INSTANCE?.isOpen == true) INSTANCE?.close()
       }

       private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext,
           AppDatabase::class.java, Constants.DATABASE_NAME).build()


   }

}