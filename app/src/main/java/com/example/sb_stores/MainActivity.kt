package com.example.sb_stores

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sb_stores.Utils.DateUtils
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.DBFileProvider
import com.example.sb_stores.database.Sales
import com.example.sb_stores.database.Year
import com.example.sb_stores.fragments.dashboard
import com.example.sb_stores.fragments.stats
import com.example.sb_stores.fragments.transaction_history
import com.example.sb_stores.fragments.user_fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
import java.time.LocalDate
import kotlin.collections.ArrayList
import kotlin.io.path.toPath


class MainActivity : AppCompatActivity() {
    lateinit var resultLauncher : ActivityResultLauncher<Intent>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//         resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // There are no request codes
//                val data: Intent? = result.data
//                val uri = data!!.data!!
//                val urinew = URI.create(uri.toString())
//
//
//                Log.d("TAG", "onActivityResult: Working $urinew")
//                    DBFileProvider().importDatabaseFile(this, urinew.toPath().toString())
//
//
//            }
//        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.home -> change(dashboard())
                R.id.transaction -> change(transaction_history())
                R.id.goal -> change(stats())
                R.id.user -> change(user_fragment())

            }
            true


        }
        val manager: FragmentManager =
            supportFragmentManager //create an instance of fragment manager
        val transaction: FragmentTransaction =
            manager.beginTransaction() //create an instance of Fragment-transaction
        transaction.replace(R.id.thisgg, dashboard(), "Frag_Top_tag")
        transaction.commit()


        GlobalScope.launch {
            val database = AppDatabase.getDatabase(this@MainActivity)

            val daily_sale = database.salesDao().getData()
            if (daily_sale.isEmpty()) {
                for (i in createYearDataset(LocalDate.now().year)){
                    Log.d("TAG", "onCreate: date addinf")
                    database.salesDao().insertData(Sales(DateUtils().getDate(i), 0,0))
                }
                database.salesDao().insertYearData(Year(LocalDate.now().year.toString(), 0))

            }
            else if (database.salesDao().getYears().last().date != LocalDate.now().year.toString()){
                for (i in createYearDataset(LocalDate.now().year)){
                    Log.d("TAG", "onCreate: date addinf")
                    database.salesDao().insertData(Sales(DateUtils().getDate(i), 0, 0))
                }
                for( cat in database.salesDao().getCategoryList()){
                    database.salesDao().addCategoryDataYear(LocalDate.now().year, category = cat.category_name)
                }

                database.salesDao().insertYearData(Year(LocalDate.now().year.toString(), 0))
            }


        }


    }

    fun change(toFragment: Fragment) {
        val manager: FragmentManager =
            supportFragmentManager //create an instance of fragment manager
        val transaction: FragmentTransaction =
            manager.beginTransaction() //create an instance of Fragment-transaction
        transaction.replace(R.id.thisgg, toFragment, "Frag_Top_tag")
        transaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createYearDataset(year: Int) : ArrayList<LocalDate>{

        val datalist = ArrayList<LocalDate>()

        for (i in 1..12) {
            if (i == 2) {
                if (DateUtils().isLeap(year)) {
                    for (j in 1..29) {
                        datalist.add(LocalDate.of(year, i, j))
                    }
                } else {
                    for (j in 1..28) {
                        datalist.add(LocalDate.of(year, i, j))
                    }
                }
            } else if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) {
                for (j in 1..31) {
                    datalist.add(
                        LocalDate.of(year, i, j)
                    )
                }
            } else if (i == 4 || i == 6 || i == 9 || i == 11) {
                for (j in 1..30) {
                    datalist.add(
                        LocalDate.of(year, i, j)
                    )
                }
            }


        }

        return datalist
    }

    fun getfile(){
//        val pickFromGallery = Intent(Intent.ACTION_GET_CONTENT)
//        pickFromGallery.type = "*/*"
//        resultLauncher.launch(pickFromGallery)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/*"
        }
        startActivityForResult(intent, 111)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                DBFileProvider().importDatabaseFile(this, uri)
            }

        }
    }








}