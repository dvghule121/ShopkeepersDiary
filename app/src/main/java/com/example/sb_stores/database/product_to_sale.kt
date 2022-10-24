package com.example.sb_stores.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productsale")
data class product_to_sale(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name")val name: String,
    @ColumnInfo(name = "mrp")val mrp: Int,
    @ColumnInfo(name = "price")val price: Int,
    @ColumnInfo(name = "qtty")val qtty: Float,
    @ColumnInfo(name = "date")val date: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "category")val categoryId: String
)