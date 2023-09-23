package com.example.sb_stores.database

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sb_stores.MainActivity
import com.example.sb_stores.Utils.DateUtils


/**
 * Represents the Room database for the SB Stores application.
 * This database manages the tables related to product sales and categories.
 */
@Database(entities = [product_to_sale::class, Sales::class, Category::class, Year::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Returns the DAO (Data Access Object) for transaction-related operations.
     */
    abstract fun apiResponseDao(): transactionDAO

    /**
     * Returns the DAO (Data Access Object) for sales-related operations.
     */
    abstract fun salesDao(): salesDao

    /**
     * Inserts external data into the database and updates related tables.
     *
     * @param activity The MainActivity instance.
     * @param productToSale List of product sales to insert.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun insertExternalData(activity: MainActivity, productToSale: List<product_to_sale>) {
        this.apiResponseDao().insertData(productToSale)
        var listofcategory = salesDao().getCategoryList()
        var listofyears = salesDao().getYears()

        for (i in productToSale) {
            val iyear = DateUtils().toLocalDate(i.date)!!.year

            if (!listofyears.contains(Year(iyear.toString(), 0))) {
                salesDao().insertYearData(Year(iyear.toString(), 0))

                for (date in activity.createYearDataset(iyear)) {
                    salesDao().insertData(Sales(DateUtils().getDate(date), 0, 0))
                }
                listofyears = salesDao().getYears()
            }

            if (!listofcategory.contains(Category(i.categoryId))) {
                salesDao().addCategory(Category(i.categoryId))
                salesDao().addCategoryData(i.categoryId)
                salesDao().updateCategoryData(i.date, (i.price * i.qtty).toInt(), i.categoryId)
                listofcategory = salesDao().getCategoryList()
            } else {
                salesDao().updateCategoryData(i.date, (i.price * i.qtty).toInt(), i.categoryId)
            }

            salesDao().updateData(i.date, (i.price * i.qtty).toInt(), (i.purchace_price * i.qtty).toInt())
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the database instance using the Singleton pattern.
         *
         * @param context The application context.
         * @return The AppDatabase instance.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(Migration1to2())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Represents a Room database migration from version 1 to version 2.
 */
class Migration1to2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS productsale_backup(id INTEGER NOT NULL, name TEXT NOT NULL, purchace_price INTEGER NOT NULL, price INTEGER NOT NULL , qtty REAL NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, category TEXT NOT NULL, PRIMARY KEY (time));")
        database.execSQL("INSERT INTO productsale_backup SELECT id, name, purchace_price, price, qtty, date, time, category FROM productsale;")
        database.execSQL("DROP TABLE IF EXISTS productsale;")
        database.execSQL("CREATE TABLE IF NOT EXISTS productsale(id INTEGER NOT NULL, name TEXT NOT NULL, purchace_price INTEGER NOT NULL, price INTEGER NOT NULL , qtty REAL NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, category TEXT NOT NULL, PRIMARY KEY (time));")
        database.execSQL("INSERT INTO productsale SELECT id, name, purchace_price, price, qtty, date, time, category FROM productsale_backup;")
        database.execSQL("DROP TABLE IF EXISTS productsale_backup;")
    }
}
