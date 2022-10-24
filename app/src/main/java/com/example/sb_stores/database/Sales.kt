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
    @ColumnInfo(name = "Steel_and_Aluminium_utensils") val steel_alluminium: Int,
    @ColumnInfo(name = "Copper_and_brass_utensils") val copper_brass: Int,
    @ColumnInfo(name = "Electronics_utensils") val electronics: Int,
    @ColumnInfo(name = "Cookware_utensils") val cookware: Int,
    @ColumnInfo(name = "Plastic_and_Fiber_utensils") val plastic: Int,
    @ColumnInfo(name = "Peti_and_Koti") val peti_koti: Int,
    @ColumnInfo(name = "Tiffin_box") val tiffin_container: Int,
    @ColumnInfo(name = "Furniture") val furniture: Int,
    @ColumnInfo(name = "Daily_Utensis") val daily_appliances: Int,
    @ColumnInfo(name = "Others") val other: Int
        )

