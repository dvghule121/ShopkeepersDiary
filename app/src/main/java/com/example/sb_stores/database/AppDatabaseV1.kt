package com.example.sb_stores.database

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sb_stores.MainActivity
import com.example.sb_stores.Utils.DateUtils


@Database(entities = [product_to_sale_v1 ::class, Sales::class, Category::class, Year::class], version = 1)
abstract class AppDatabaseV1 : RoomDatabase() {

    abstract fun apiResponseDao(): apiResponseDao_v1

    companion object {

    }
}

