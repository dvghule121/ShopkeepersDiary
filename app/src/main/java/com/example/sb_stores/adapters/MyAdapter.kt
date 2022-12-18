package com.example.sb_stores.adapters

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


class MyAdapter( val myContext: Context, fm: FragmentManager, var totalTabs: Int) :
    FragmentPagerAdapter(fm) {
    var date= emptyList<LocalDate>()
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

    fun setData(dates: List<LocalDate>){
        this.date = dates
        notifyDataSetChanged()
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return date.size
    }
}