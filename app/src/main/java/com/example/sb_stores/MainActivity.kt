package com.example.sb_stores

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sb_stores.Utils.DateUtils
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.DBFileProvider
import com.example.sb_stores.database.Sales
import com.example.sb_stores.database.Year
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate


class MainActivity : AppCompatActivity() {
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var navController: NavController
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        bottomNavigationView.setupWithNavController(navController)


        GlobalScope.launch {
            val database = AppDatabase.getDatabase(this@MainActivity)

            val daily_sale = database.salesDao().getData()
            if (daily_sale.isEmpty()) {
                for (i in createYearDataset(LocalDate.now().year)) {
                    Log.d("TAG", "onCreate: date addinf")
                    database.salesDao().insertData(Sales(DateUtils().getDate(i), 0, 0))
                }
                database.salesDao().insertYearData(Year(LocalDate.now().year.toString(), 0))

            } else if (!database.salesDao().getYears()
                    .contains(Year(LocalDate.now().year.toString(), 0))
            ) {
                for (i in createYearDataset(LocalDate.now().year)) {
                    Log.d("TAG", "onCreate: date addinf")
                    database.salesDao().insertData(Sales(DateUtils().getDate(i), 0, 0))
                }


                database.salesDao().insertYearData(Year(LocalDate.now().year.toString(), 0))
            }


        }


    }

    fun change(toFragment: Int, bundle:Bundle = Bundle()) {
        runOnUiThread {
            navController.navigate(toFragment, bundle)
        }

    }

    fun requestDb(): AppDatabase {
        return AppDatabase.getDatabase(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createYearDataset(year: Int): ArrayList<LocalDate> {

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

    fun getfile() {

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
                DBFileProvider().importDatabaseFile(this, this, uri)
            }

        }
    }


}