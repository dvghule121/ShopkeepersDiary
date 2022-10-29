package com.example.sb_stores.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Month

@Entity(tableName = "sales_data")
data class Sales (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "sale") val daily_sale: Int,
    @ColumnInfo(name = "purchased") val daily_pur: Int

    )

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "category_name") val category_name:String
    )

@Entity(tableName = "year")
data class Year(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "year") val date: String,
    @ColumnInfo(name = "sale") val daily_sale: Int,

    )