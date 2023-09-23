package com.example.sb_stores.fragments


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.dynocodes.graphosable.BarData
import com.dynocodes.graphosable.Slice

import com.example.sb_stores.Utils.DateUtils
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.Sales
import com.example.sb_stores.database.Year
import com.example.sb_stores.fragments.ui.components.Graphs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import kotlin.collections.ArrayList


class dashboard : Fragment(), AdapterView.OnItemSelectedListener {

    private var graphs = Graphs()
    private lateinit var pieView: ComposeView

    @RequiresApi(Build.VERSION_CODES.O)
    private var month: Int = LocalDate.now().month.value

    @RequiresApi(Build.VERSION_CODES.O)
    private var year: Int = 2023
    lateinit var year_spinner: Spinner
    private var db: List<Sales>? = null
    private lateinit var AppDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        pieView = view.findViewById(R.id.composeView)
        year_spinner = view.findViewById(R.id.spinner_filter_dashboard)
        year_spinner.onItemSelectedListener = this
        val act = requireActivity() as MainActivity



        GlobalScope.launch {
            AppDatabase = act.requestDb()
            val database = AppDatabase.salesDao()
            val salesDao = database


            val sum = database.getDataOfDate(DateUtils().getTodaysDate())
            var amount = 0
            requireActivity().runOnUiThread {
                if (sum.isNotEmpty()) amount = sum[0].daily_sale
                view.findViewById<TextView>(R.id.sale_amount_month).text = "₹ ${formatAmount(amount.toDouble())}"

            }

            try {
                val s = salesDao.getDataOfDate(DateUtils().getTodaysDate()).get(0).daily_sale
                val p = salesDao.getDataOfDate(DateUtils().getTodaysDate()).get(0).daily_pur
                view.findViewById<TextView>(R.id.profit_daily).text =
                    "Profit ₹ " + (s - p).toString()
            } catch (e: Exception) {
                view.findViewById<TextView>(R.id.profit_daily).text = "Profit ₹ 0"
            }


//            getMonthlyCategory(month, year)
            getDailyCategory()
            year_spinner.setSelection(2)
        }
        view.findViewById<FloatingActionButton>(R.id.add_cat).setOnClickListener {
            val act = requireActivity() as MainActivity
            act.change(R.id.action_dashboardFragment_to_create_transaction2)
        }
        return view
    }

    //    fun addDataset(pieChart: SimplePieChart, dataset: ArrayList<SimplePieChart.Slice>) {
//        requireActivity().runOnUiThread{
//            for (i in dataset) {
//                pieChart.addSlice(i)
//            }
//        }
//
//    }

    fun formatAmount(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN")) as DecimalFormat
        formatter.applyPattern("#,##,##0")
        return formatter.format(amount)
    }




    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyCategory() {
        GlobalScope.launch {

            val salesDao =
                AppDatabase.salesDao()

            val dataset = ArrayList<Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                if (salesDao.getCategoryData(DateUtils().getTodaysDate(), i.category_name) != 0) {
                    dataset.add(
                        Slice(
                            salesDao.getCategoryData(DateUtils().getTodaysDate(), i.category_name)
                                .toFloat().toInt(),
                            i.category_name.replace("_", " ")
                        )
                    )
                }
            }
            requireActivity().runOnUiThread {
                graphs.setPieChartView(dataset, pieView, requireContext())
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthlyCategory(month_to: Int, year: Int) {
        GlobalScope.launch {
            val salesDao = AppDatabase.salesDao()

            val dataset = ArrayList<Slice>()
            val arrayList = salesDao.getCategoryList()

            for (i in arrayList) {
                val categoryData = salesDao.getCategoryDataMonth(
                    "$month_to".format("%02d", month_to),
                    year,
                    i.category_name
                )

                if (categoryData != 0) {
                    dataset.add(Slice(categoryData.toFloat().toInt(), i.category_name.replace("_", " ")))
                }
            }

            requireActivity().runOnUiThread {
                graphs.setPieChartView(dataset, pieView, requireContext())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearlyCategory(year: Int) {
        GlobalScope.launch {
            val salesDao = AppDatabase.salesDao()

            val dataset = ArrayList<Slice>()
            val arrayList = salesDao.getCategoryList()

            for (i in arrayList) {
                val categoryData = salesDao.getCategoryDataYear(year, i.category_name)

                if (categoryData != 0) {
                    dataset.add(Slice(categoryData.toFloat().toInt(), i.category_name.replace("_", " ")))
                }
            }

            requireActivity().runOnUiThread {
                graphs.setPieChartView(dataset, pieView, requireContext())
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {


        if (p0!!.selectedItem == "Yearly") {
            getYearlyCategory(year)
        } else if (p0.selectedItem == "Monthly") {
            getMonthlyCategory(month, year)
        } else {
            getDailyCategory()
        }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


}