package com.example.sb_stores

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.sb_stores.fragments.transaction_page
import java.time.LocalDate
import java.util.*


class MyAdapter(val date: List<LocalDate>, val myContext: Context, fm: FragmentManager, var totalTabs: Int) :
    FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getItem(position: Int): Fragment {

        val current_date = date[position]
        val date = "${
            String.format(
                "%02d",
                current_date.dayOfMonth
            )
        }-${String.format("%02d", current_date.monthValue)}-${current_date.year}"

        val bundle = Bundle()
        bundle.putString("date", date)
        Log.d("TAG", "getItem: ${date}, ${current_date.year}")

        val habit_fragment = transaction_page()
        habit_fragment.arguments = bundle


        return habit_fragment
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}