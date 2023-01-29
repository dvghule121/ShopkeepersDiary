package com.example.sb_stores.database

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sb_stores.MainActivity
import com.example.sb_stores.Utils.DateUtils
import java.time.LocalDate

@Database(entities = [product_to_sale::class, Sales::class, Category::class, Year::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun apiResponseDao(): transactionDAO

    abstract fun salesDao(): salesDao

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertExternalData(activity: MainActivity,productToSale: List<product_to_sale>){

        this.apiResponseDao().insertData(productToSale)
        var listofcategory = salesDao().getCategoryList()
        var listofyears = salesDao().getYears()
        for (i in productToSale) {
            val iyear = DateUtils().toLocalDate(i.date)!!.year
            if (!listofyears.contains(Year(iyear.toString(), 0 ))){
                salesDao().insertYearData(Year(iyear.toString(),0))
                for (i in activity.createYearDataset(iyear)){
                    Log.d("TAG", "onCreate: date addinf")
                    salesDao().insertData(Sales(DateUtils().getDate(i), 0, 0))
                }
                listofyears = salesDao().getYears()
            }

            if (!listofcategory.contains(Category(i.categoryId))) {
                salesDao().addCategory(Category(i.categoryId))
                salesDao().addCategoryData(i.categoryId)
                salesDao().updateCategoryData(i.date, (i.price*i.qtty).toInt(), i.categoryId)
                listofcategory = salesDao().getCategoryList()

            } else {
                salesDao().updateCategoryData(i.date, (i.price*i.qtty).toInt(), i.categoryId)
            }

            salesDao().updateData(i.date, (i.price*i.qtty).toInt(), (i.purchace_price*i.qtty).toInt())


        }





    }

    companion object {


        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                return instance
            }
        }



    }
}