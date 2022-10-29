package com.example.sb_stores.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [product_to_sale::class, Sales::class, Category::class, Year::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun apiResponseDao(): transactionDAO

    abstract fun salesDao(): salesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance

                return instance
            }
        }


    }
}