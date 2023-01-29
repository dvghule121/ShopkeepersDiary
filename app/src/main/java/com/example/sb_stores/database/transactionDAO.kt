package com.example.sb_stores.database

import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

var dataReplaced = false
@Dao
interface transactionDAO {
    @Query("SELECT * FROM productsale")
    fun getData():List<product_to_sale>

    @Query("SELECT * FROM productsale where date = :date")
    fun getDataOfDate(date:String):List<product_to_sale>

    @Query("SELECT * FROM productsale where id = :id")
    fun getDataById(id: Int):List<product_to_sale>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(productToSale: product_to_sale)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(productToSale: List<product_to_sale>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateData(productToSale: product_to_sale)

    @Query("DELETE FROM productsale where id = :id")
    fun delete(id: Int)

    @Query("SELECT *  FROM  productsale where name like :name || '%' GROUP BY name")
    fun getSearch(name:String): List<product_to_sale>




}