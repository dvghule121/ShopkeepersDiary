package com.example.sb_stores.database

import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface apiResponseDao_v1 {
    @Query("SELECT * FROM productsale")
    fun getData():List<product_to_sale_v1>

    @Query("SELECT * FROM productsale where date = :date")
    fun getDataOfDate(date:String):List<product_to_sale_v1>

    @Query("SELECT * FROM productsale where id = :id")
    fun getDataById(id: Int):List<product_to_sale_v1>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(productToSale: product_to_sale_v1)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(productToSale: List<product_to_sale_v1>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateData(productToSale: product_to_sale_v1)

    @Query("DELETE FROM productsale where id = :id")
    fun delete(id: Int)

    @Query("SELECT *  FROM  productsale where name like :name || '%' GROUP BY name")
    fun getSearch(name:String): List<product_to_sale_v1>




}