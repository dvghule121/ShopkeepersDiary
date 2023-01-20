package com.example.sb_stores.database

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class DBFileProvider : FileProvider() {

    fun getDatabaseURI(c: Context, dbName: String?): String {
        val file: File = c.getDatabasePath(dbName)
        return getFileUri(c, file)
    }

    private fun getFileUri(context: Context, file: File): String {
        val output  = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"AppDatabase.db")
        if(!output.exists()) return file.copyTo(output).path;
        else {
            output.delete()
            return file.copyTo(output).path
        }
    }

    fun backupDatabase(activity: AppCompatActivity?): String {
        val db= DBFileProvider().getDatabaseURI(activity!!, "app_database")
        Log.d("TAG", "backupDatabase: $db")
        return db
//        sendEmail(activity, uri)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun importDatabaseFile(context: Context, uri: Uri) {
        GlobalScope.launch {

            val inputStream = context.contentResolver.openInputStream(uri)
            val tempDbFile = File(context.getExternalFilesDir(null), "temp_db.db")
            val outputStream = FileOutputStream(tempDbFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val tempDb =
                Room.databaseBuilder(context, AppDatabase::class.java, tempDbFile.path).build()
            val dataFromFileSales = tempDb.salesDao().getData()
            val dataFromFileEntry = tempDb.apiResponseDao().getData()
            val dataFromFileYear = tempDb.salesDao().getYears()
            tempDb.close()
//            tempDbFile.delete()

            // Add the data from the file to the existing data
            val mydb =
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()
            val currentData = mydb.salesDao().getData()
            val currentEntry = tempDb.apiResponseDao().getData()
            val updatedData = currentData + dataFromFileSales
            val currentDataYear = mydb.salesDao().getYears()
            val updatedDataYear = currentDataYear + dataFromFileYear
            val updatedDataEntry = currentEntry + dataFromFileEntry
            val currentCategory = mydb.salesDao().getCategoryList()


            for(i in tempDb.salesDao().getCategoryList()){
                if (!currentCategory.contains(i)){
                    mydb.salesDao().addCategory(i)
                    mydb.salesDao().addCategoryData(currentEntry.first().date, i.category_name)
                    mydb.salesDao().addCategoryDataYear(currentDataYear.lastIndex, i.category_name )


                }
            }
            mydb.salesDao().insertData(updatedData)
            mydb.apiResponseDao().insertData(updatedDataEntry)
            mydb.salesDao().insertYearData(updatedDataYear)
            mydb.close()
        }


    }}