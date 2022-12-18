package com.example.sb_stores.database

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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

    fun importDatabaseFile(context: Context, filename: String) {
        copyDataFromOneToAnother(filename, context.getDatabasePath("app_database").path)

    }

    private fun copyDataFromOneToAnother(fromPath: String, toPath: String) {
        val inStream = File(fromPath).inputStream()
        val outStream = FileOutputStream(toPath)

        inStream.use { input ->
            outStream.use { output ->
                input.copyTo(output)
            }
        }
    }


}