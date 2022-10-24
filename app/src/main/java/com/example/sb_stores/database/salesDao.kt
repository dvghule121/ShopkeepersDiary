package com.example.sb_stores.database

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface salesDao {
    @Query("SELECT * FROM sales_data")
    fun getData():List<Sales>

    @Query("SELECT * FROM sales_data where date = :date")
    fun getDataOfDate(date:String):List<Sales>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(productToSale: Sales)

    @Query("UPDATE sales_data SET sale = sale+:amount WHERE date = :date")
    fun updateData(date: String, amount: Int)

    @RawQuery
    fun rawQueryUpdate(query: SupportSQLiteQuery):Any

    @RawQuery
    fun rawQueryGet(query: SupportSQLiteQuery):Int
//
//    @Query("UPDATE sales_data SET :category. WHERE date = :date")
    fun updateCategoryData(date: String, amount: Int, category: String){
        val query = SimpleSQLiteQuery("UPDATE sales_data SET $category = $category + $amount WHERE date = '$date'")
        rawQueryUpdate(query)
    }

    fun getCategoryData(date: String, category: String): Int{
        val query = SimpleSQLiteQuery("SELECT $category FROM sales_data WHERE date = '$date'")
        return rawQueryGet(query) as Int
    }

    fun addCategoryData(date: String, category: String): Int{
        val query = SimpleSQLiteQuery("ALTER TABLE sales_data ADD COLUMN $category INTEGER")
        return rawQueryUpdate(query) as Int
    }

    @Query("DELETE FROM productsale")
    fun delete()
}