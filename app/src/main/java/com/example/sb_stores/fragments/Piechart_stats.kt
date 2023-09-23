package com.example.sb_stores.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.dynocodes.graphosable.Slice
import com.example.sb_stores.Utils.DateUtils
import com.example.sb_stores.R
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.fragments.ui.components.Graphs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import kotlin.collections.ArrayList


class piechart_stats : Fragment(), AdapterView.OnItemSelectedListener {
    private var awesomePieChart: ComposeView? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private var month: Int = LocalDate.now().month.value
    @RequiresApi(Build.VERSION_CODES.O)
    private var day: String = DateUtils().getTodaysDate()

    @RequiresApi(Build.VERSION_CODES.O)
    private var year: Int = LocalDate.now().year
    lateinit var year_spinner: Spinner
    lateinit var month_spinner: Spinner
    lateinit var month_spinner_adapter: ArrayAdapter<String>
    lateinit var AppDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_piechart_stats, container, false)
        month_spinner = view.findViewById(R.id.spinner_filter_data)
        year_spinner = view.findViewById(R.id.spinner_filter_piestats)
        year_spinner.onItemSelectedListener = this
        month_spinner.onItemSelectedListener = this
        awesomePieChart =
            view.findViewById<ComposeView>(R.id.piechart)

        GlobalScope.launch {
            AppDatabase = com.example.sb_stores.database.AppDatabase.getDatabase(requireContext())
            year_spinner.setSelection(2)

        }
        return view
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyCategory(day: String) {
        GlobalScope.launch {

            val salesDao =
                AppDatabase.salesDao()

            val dataset = ArrayList<Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                if (salesDao.getCategoryData(day, i.category_name) != 0) {
                    dataset.add(
                        Slice(
                            salesDao.getCategoryData(day, i.category_name)!!.toFloat().toInt(),
                            i.category_name
                        )
                    )
                }
            }

            Graphs().setPieChartView(dataset, requireView().findViewById<ComposeView>(R.id.piechart), requireContext())

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthlyCategory(month_to: Int, year: Int) {
        GlobalScope.launch {

            val salesDao =
                AppDatabase.salesDao()

            val dataset = ArrayList<Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                if (salesDao.getCategoryDataMonth(
                        String.format("%02d", month_to),
                        year,
                        i.category_name
                    ) != 0
                ) {
                    dataset.add(
                       Slice(
                            salesDao.getCategoryDataMonth(
                                String.format("%02d", month_to),
                                year,
                                i.category_name
                            ).toFloat().toInt(),
                            i.category_name
                        )
                    )
                } else {
                    Log.d(
                        "TAG",
                        "getMonthlyCategory: ${"$month".format("%02d", month_to)}, $month_to"
                    )
                }
            }
            Graphs().setPieChartView(dataset, requireView().findViewById<ComposeView>(R.id.piechart), requireContext())


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearlyCategory(year: Int) {
        GlobalScope.launch {

            val salesDao =
                AppDatabase.salesDao()

            val dataset = ArrayList<Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                if (salesDao.getCategoryDataYear(year, i.category_name) != 0) {
                    dataset.add(
                       Slice(
                            salesDao.getCategoryDataYear(year, i.category_name).toFloat().toInt(),
                            i.category_name
                        )
                    )
                } else {
                    Log.d(
                        "TAG",
                        "getYearlyCategory: ${
                            salesDao.getCategoryDataMonth(
                                String().format(
                                    "%02d",
                                    month
                                ), year, i.category_name
                            )
                        }"
                    )
                }
            }
            Graphs().setPieChartView(dataset, requireView().findViewById<ComposeView>(R.id.piechart), requireContext())


        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)


        if (parent.id == R.id.spinner_filter_data){
            if (year_spinner.selectedItem == "Yearly")  year = parent.selectedItem.toString().toInt()
            else if (year_spinner.selectedItem == "Monthly") month = pos+1
            else if (year_spinner.selectedItem == "Daily") day = parent.selectedItem.toString()

        }
        else{

            if (parent.selectedItem == "Yearly"){
                GlobalScope.launch {
                    val years = AppDatabase.salesDao().getYears()
                    val temp = java.util.ArrayList<String>()
                    if (years.isNotEmpty()) {
                        for (i in years) {
                            temp.add(i.date)
                        }
                    } else {
                        temp.add(LocalDate.now().year.toString())
                    }
                    requireActivity().runOnUiThread {

                        month_spinner_adapter =
                            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, temp)
                        month_spinner_adapter!!.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
                        month_spinner!!.adapter = month_spinner_adapter

                    }
                    getYearlyCategory(year)
                }
            }
            else if  (parent.selectedItem == "Monthly"){
                val a = java.util.ArrayList<String>()
                for (i in 1..12) {
                    a.add(Month.of(i).toString())
                }


                month_spinner_adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, a)
                month_spinner_adapter!!.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
                month_spinner!!.adapter = month_spinner_adapter
                month_spinner.setSelection(month-1)
                getMonthlyCategory(month, year)

            }
            else{
                val a = java.util.ArrayList<String>()
                for (i in 1..Month.of(month).length(DateUtils().isLeap(year))) {
                    a.add(DateUtils().getDate(LocalDate.of(year, month, i)))
                }

                month_spinner_adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, a)
                month_spinner_adapter.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
                month_spinner.adapter = month_spinner_adapter
                month_spinner.setSelection(LocalDate.now().dayOfMonth-1)
                getDailyCategory(DateUtils().getTodaysDate())
            }

        }

        if (year_spinner.selectedItem == "Daily") {


            getDailyCategory(day)
        } else if (year_spinner.selectedItem == "Yearly") {

            getYearlyCategory(year)
        } else {

            getMonthlyCategory(month,year)
        }




    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }


}