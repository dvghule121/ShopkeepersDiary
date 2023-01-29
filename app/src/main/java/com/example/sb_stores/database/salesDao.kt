package com.example.sb_stores.database

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.sb_stores.Utils.DateUtils

@Dao
@RequiresApi(Build.VERSION_CODES.O)
interface salesDao {
    @Query("SELECT * FROM sales_data")
    fun getData():List<Sales>

    @Query("SELECT * FROM categories")
    fun getCategoryList():List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCategory(category: Category)



    @Query("SELECT * FROM sales_data where date = :date")
    fun getDataOfDate(date:String):List<Sales>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(productToSale: Sales)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(productToSale: List<Sales>)

    @Query("UPDATE sales_data SET sale = sale+:amount , purchased = purchased +:pur WHERE date = :date")
    fun updateData(date: String, amount: Int, pur:Int)

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

    fun InsertCategoryData(date: String, amount: Int, category: String){
        val query = SimpleSQLiteQuery("UPDATE sales_data SET $category = $amount WHERE date = '$date'")
        rawQueryUpdate(query)
    }

    fun getCategoryData(date: String, category: String): Int{
        val query = SimpleSQLiteQuery("SELECT $category FROM sales_data WHERE date = '$date'")
        return rawQueryGet(query) as Int
    }

    fun getCategoryDataMonth(month: String, year: Int, category: String):Int {
        val query = SimpleSQLiteQuery("SELECT sum($category) FROM sales_data WHERE date like '%$month-$year'")
        return rawQueryGet(query) as Int
    }

    fun getCategoryDataYear(year: Int, category: String):Int {
        val query = SimpleSQLiteQuery("SELECT sum($category) FROM sales_data WHERE date like '%-$year'")
        return rawQueryGet(query) as Int
    }


    fun addCategoryData(date: String, category: String){
        val query = SimpleSQLiteQuery("ALTER TABLE sales_data ADD COLUMN $category INTEGER Default 0")
        rawQueryUpdate(query)
        addCategory(Category(category))
        val saleList = getData()
        for (i in saleList){
            InsertCategoryData(i.date, 0, category)
        }
    }

    fun addCategoryData(category: String){
        val query = SimpleSQLiteQuery("ALTER TABLE sales_data ADD COLUMN $category INTEGER Default 0")
        try {
            rawQueryUpdate(query)
        }
        catch (e :Exception){
            return
        }

        addCategory(Category(category))
        val saleList = getData()
        for (i in saleList){
            InsertCategoryData(i.date, 0, category)
        }
    }

    fun addCategoryDataYear(year: Int, category: String){
        val saleList = getData()
        for (i in saleList){
            if (DateUtils().toLocalDate(i.date)!!.year  == year){
                Log.d("gg", "addCategoryDataYear: something")
                InsertCategoryData(i.date, 0, category)
            }

        }
    }

    @Query("DELETE FROM productsale")
    fun delete()

    fun getMonthlySale(month:Int, year: Int): Int{
        val saleList = getData()
        var sum = 0
        for (i in saleList){
            val date = DateUtils().toLocalDate(i.date)
            if (month == date!!.monthValue-1 && year == date!!.year) sum = sum + i.daily_sale
        }

        return sum
    }

    fun getMonthlyPurchace(month:Int, context: Context, year: Int):Int{
        val saleList = getData()
        var sum = 0
        for (i in saleList){
            val date = DateUtils().toLocalDate(i.date)
            if (month == date!!.monthValue-1 && year == date!!.year) sum = sum + i.daily_pur
        }

        return sum
    }


    fun getYearlySale(year:Int): Int{
        val saleList = getData()
        var sum = 0

        for (i in saleList){
            val date = DateUtils().toLocalDate(i.date)
            if (year == date!!.year ) sum = sum + i.daily_sale
        }

        return sum
    }

    fun getYearlyPurchace(year:Int, context: Context):Int{
        val saleList = AppDatabase.getDatabase(context).apiResponseDao().getData()
        var sum = 0
        for (i in 1..12){
            val sale_month = getMonthlyPurchace(i,context, year)
            sum += sale_month
        }

        return sum
    }

    @Query("Select * from year")
    fun getYears():List<Year>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertYearData(year: Year)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertYearData(year: List<Year>)

    @Query("UPDATE year SET sale = sale+:amount WHERE year = :date")
    fun updateYearData(date: String, amount: Int)
}