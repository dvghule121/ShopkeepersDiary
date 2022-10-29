package com.example.sb_stores.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.example.sb_stores.MainActivity
import com.example.sb_stores.adapters.MyAdapter
import com.example.sb_stores.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [transaction_history.newInstance] factory method to
 * create an instance of this fragment.
 */
class transaction_history :  Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction_history, container, false)

        tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        viewPager = view.findViewById<ViewPager>(R.id.viewPager2)

//        val mydb = DatabaseHandler(requireContext())
//        val dateData = mydb.getAllTasksDate()
//          val s = dateData.asReversed()

        if (true) {

            for(i in getAllDateOfMonth().reversed() ){

                val dt1 = i
                val format2: DateFormat = SimpleDateFormat("EEE")
                val finalDay = i.dayOfWeek
                Log.d("TAG", "onCreateView: $dt1")

                val date_view= inflater.inflate(R.layout.card_for_date,container, false)


                date_view.findViewById<TextView>(R.id.textView9).text = finalDay.name.slice(0..2)
                date_view.findViewById<TextView>(R.id.textView10).text = dt1.dayOfMonth.toString()
                tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(date_view))

            }



            val adapter = MyAdapter(
                getAllDateOfMonth().reversed(),
                requireContext(),
                childFragmentManager,
                tabLayout!!.tabCount
            )
            viewPager!!.adapter = adapter


            viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

            tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager!!.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }

                override fun onTabReselected(tab: TabLayout.Tab) {

                }
            })
        }

        view.findViewById<FloatingActionButton>(R.id.create_transaction).setOnClickListener {
            val act = activity as MainActivity
            act.change(create_transaction())

        }



        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllDateOfMonth(): List<LocalDate> {



        val today = LocalDate.now()
        val datesOfThisMonth = mutableListOf<LocalDate>()
        for (daysNo in 1 until LocalDate.now().dayOfMonth+1) {

            datesOfThisMonth.add(LocalDate.of(today.year, today.monthValue,daysNo))
        }
        return datesOfThisMonth
    }



}