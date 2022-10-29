package com.example.sb_stores.fragments


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.samplechart.SimplePieChart.SimplePieChart
import com.example.sb_stores.DateUtils
import com.example.sb_stores.R
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.Year
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class dashboard : Fragment(), AdapterView.OnItemSelectedListener {
    private var param1: String? = null
    private var param2: String? = null
    private var awesomePieChart:SimplePieChart? = null
    @RequiresApi(Build.VERSION_CODES.O)
    private var month : Int = LocalDate.now().month.value
    @RequiresApi(Build.VERSION_CODES.O)
    private var year: Int = LocalDate.now().year
    lateinit var year_spinner: Spinner

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

        year_spinner = view.findViewById(R.id.spinner_filter_dashboard)
        year_spinner.onItemSelectedListener = this
        awesomePieChart =
            view.findViewById<View>(R.id.piechart) as SimplePieChart

        GlobalScope.launch {
            val database = AppDatabase.getDatabase(requireContext()).salesDao()
            val salesDao =
                AppDatabase.getDatabase(requireActivity().applicationContext).salesDao()


            val sum = database.getDataOfDate(DateUtils().getTodaysDate())
            var amount = 0
            requireActivity().runOnUiThread{
                if (sum.isNotEmpty()) amount = sum[0].daily_sale
                view.findViewById<TextView>(R.id.sale_amount_month).text = "₹ $amount"

            }

            try {
                val s = salesDao.getDataOfDate(DateUtils().getTodaysDate()).get(0).daily_sale
                val p = salesDao.getDataOfDate(DateUtils().getTodaysDate()).get(0).daily_pur
                view.findViewById<TextView>(R.id.profit_daily).text = "Profit ₹ "+ (s - p).toString()
            }
            catch (e : Exception){
                view.findViewById<TextView>(R.id.profit_daily).text = "Profit ₹ 0"
            }


//            getMonthlyCategory(Date().month, LocalDate.now().year)
            getDailyCategory()
            year_spinner.setSelection(2)
        }
        return view
    }

    fun addDataset(pieChart: SimplePieChart, dataset: ArrayList<SimplePieChart.Slice>) {
        requireActivity().runOnUiThread{
            for (i in dataset) {
                pieChart.addSlice(i)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyCategory(){
        GlobalScope.launch {

            val salesDao =
                AppDatabase.getDatabase(requireActivity().applicationContext).salesDao()

            val dataset = ArrayList<SimplePieChart.Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                if (salesDao.getCategoryData(DateUtils().getTodaysDate(), i.category_name) != 0) {
                    dataset.add(
                        SimplePieChart.Slice(
                            (Math.random() * 16777215).toInt() or (0xFF shl 24),
                            salesDao.getCategoryData(DateUtils().getTodaysDate(), i.category_name).toFloat(),
                            i.category_name
                        )
                    )
                }
            }
            addDataset(awesomePieChart!!, dataset as ArrayList<SimplePieChart.Slice>)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthlyCategory(month: Int, year: Int){
        GlobalScope.launch {

            val salesDao =
                AppDatabase.getDatabase(requireActivity().applicationContext).salesDao()

            val dataset = ArrayList<SimplePieChart.Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                if (salesDao.getCategoryDataMonth(String().format("%02d",month),year, i.category_name) != 0) {
                    dataset.add(
                        SimplePieChart.Slice(
                            (Math.random() * 16777215).toInt() or (0xFF shl 24),
                            salesDao.getCategoryDataMonth(String().format("%02d",month),year, i.category_name).toFloat(),
                            i.category_name
                        )
                    )
                }
                else{
                    Log.d("TAG", "getMonthlyCategory: ${salesDao.getCategoryDataMonth(String().format("%02d",month),year, i.category_name)}")
                }
            }
            addDataset(awesomePieChart!!, dataset as ArrayList<SimplePieChart.Slice>)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearlyCategory(){
        GlobalScope.launch {

            val salesDao =
                AppDatabase.getDatabase(requireActivity().applicationContext).salesDao()

            val dataset = ArrayList<SimplePieChart.Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                for (j in salesDao.getYears()){
                if (salesDao.getCategoryDataYear(j.date.toInt(), i.category_name) != 0) {
                    dataset.add(
                        SimplePieChart.Slice(
                            (Math.random() * 16777215).toInt() or (0xFF shl 24),
                            salesDao.getCategoryDataYear(j.date.toInt(), i.category_name).toFloat(),
                            i.category_name
                        )
                    )
                }
                else{
                    Log.d("TAG", "getMonthlyCategory: ${salesDao.getCategoryDataMonth(String().format("%02d",month),year, i.category_name)}")
                }}
            }
            addDataset(awesomePieChart!!, dataset as ArrayList<SimplePieChart.Slice>)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        if (p0!!.id == R.id.spinner_month) {
//
//            month = p2
//        }else {
//            year = p0.selectedItem.toString().toInt()
//            Log.d("TAG", "onItemSelected: Item seell $year")
//        }
        awesomePieChart!!.removeAllSices()
        if (p0!!.selectedItem == "Yearly"){
            getYearlyCategory()
        }
        else if (p0.selectedItem == "Monthly"){
            getMonthlyCategory(month, year)
        }
        else{
            getDailyCategory()
        }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


}