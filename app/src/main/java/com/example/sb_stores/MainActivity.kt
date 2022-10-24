package com.example.sb_stores

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.Sales
import com.example.sb_stores.fragments.dashboard
import com.example.sb_stores.fragments.transaction_history
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.home -> change(dashboard())
                R.id.transaction -> change(transaction_history())
//                R.id.goal -> change(goals())
//                R.id.user -> change(goal_detail())

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
                    database.salesDao().insertData(Sales(DateUtils().getDate(i), 0,0,0,0,0,0,0,0,0,0,0))
                }

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


}