package com.example.sb_stores.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productsale")
data class product_to_sale_v1(

    @PrimaryKey(autoGenerate = true)val id: Int,
    @ColumnInfo(name = "name")val name: String,
    @ColumnInfo(name = "purchace_price")val purchace_price: Int,
    @ColumnInfo(name = "price")val price: Int,
    @ColumnInfo(name = "qtty")val qtty: Float,
    @ColumnInfo(name = "date")val date: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "category")val categoryId: String
)