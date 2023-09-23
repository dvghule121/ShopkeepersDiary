package com.example.sb_stores.database

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.room.Room
import com.example.sb_stores.MainActivity
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
    fun importDatabaseFile(context: Context, activity: MainActivity, uri: Uri) {
        GlobalScope.launch {

            val inputStream = context.contentResolver.openInputStream(uri)
            val tempDbFile = File(context.getExternalFilesDir(null), "temp_db.db")
            val outputStream = FileOutputStream(tempDbFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            var currentEntry : ArrayList<product_to_sale>

            try{
                val tempDb =
                    Room.databaseBuilder(context, AppDatabaseV1::class.java, tempDbFile.path)
                        .build()
                val dataFromFileEntry = tempDb.apiResponseDao().getData()


//            tempDbFile.delete()

                // Add the data from the file to the existing data

                currentEntry = ArrayList()

                for (i in dataFromFileEntry) {
                    currentEntry.add(
                        product_to_sale(
                            i.id,
                            i.name,
                            i.purchace_price,
                            i.price,
                            i.qtty,
                            i.date,
                            i.time,
                            i.categoryId
                        )
                    )
                }
            }
            catch (e:Exception){
                val tempDb =
                    Room.databaseBuilder(context, AppDatabase::class.java, tempDbFile.path)
                        .build()
                currentEntry = tempDb.apiResponseDao().getData() as ArrayList<product_to_sale> /* = java.util.ArrayList<com.example.sb_stores.database.product_to_sale> */
            }


            val mydb =
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()

            mydb.insertExternalData(activity,currentEntry)
            mydb.close()
        }


    }

    fun generateAlterTableQuery(tableName: String, columns: List<String>): String {
        val sb = StringBuilder("ALTER TABLE $tableName ADD COLUMN ")
        columns.forEachIndexed { index, columnName ->
            sb.append("$columnName INTEGER NOT NULL DEFAULT 0")
            if (index != columns.lastIndex) {
                sb.append(", ")
            }
        }
        return sb.toString()
    }

}