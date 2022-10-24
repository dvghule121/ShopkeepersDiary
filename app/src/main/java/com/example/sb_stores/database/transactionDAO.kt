package com.example.sb_stores.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface transactionDAO {
    @Query("SELECT * FROM productsale")
    fun getData():List<product_to_sale>

    @Query("SELECT * FROM productsale where date = :date")
    fun getDataOfDate(date:String):List<product_to_sale>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(productToSale: product_to_sale)

    @Query("DELETE FROM productsale")
    fun delete()
}