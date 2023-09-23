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
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.dynocodes.graphosable.BarData
import com.dynocodes.graphosable.Slice
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R
import com.example.sb_stores.Utils.DateUtils
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.fragments.ui.components.Graphs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [stats.newInstance] factory method to
 * create an instance of this fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)

class stats : Fragment(), AdapterView.OnItemSelectedListener {

    // UI components
    private lateinit var totalProfit: TextView
    private lateinit var totalSale: TextView
    private lateinit var barView: ComposeView
    private lateinit var pieView: ComposeView
    private lateinit var yearSpinner: Spinner
    private lateinit var yearSpinnerAdapter: ArrayAdapter<String>
    private lateinit var radioGroup: RadioGroup

    // Data and utilities
    private val graphs = Graphs()
    private val dateUtils = DateUtils()
    private var month: Int = LocalDate.now().month.value
    private var year: Int = LocalDate.now().year
    private lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sale_stats, container, false)
        initializeUI(view)
        setUpRadioGroupListener()
        initializeData()
        appDatabase = (activity as MainActivity).requestDb()
        return view
    }

    private fun initializeUI(view: View) {
        // Initialize UI components
        totalSale = view.findViewById(R.id.tv_total_sale)
        totalProfit = view.findViewById(R.id.tv_avg_sale)
        barView = view.findViewById(R.id.chart_compose_view)
        pieView = view.findViewById(R.id.piechart)
        yearSpinner = view.findViewById(R.id.spinner_filter_dashboard2)
        yearSpinner.onItemSelectedListener = this
        radioGroup = view.findViewById(R.id.radioGroup)
    }

    private fun setUpRadioGroupListener() {
        // Set up radio button change listener
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButton1 -> handleWeeklyRadioButton()
                R.id.radioButton2 -> handleMonthlyRadioButton()
                R.id.radioButton3 -> handleYearlyRadioButton()
            }
        }
    }

    private fun initializeData() {
        val act = requireActivity() as MainActivity
        GlobalScope.launch {
            appDatabase = act.requestDb()
        }
        val dayList = (1..Month.of(month).length(DateUtils().isLeap(year))).map {
            DateUtils().getDate(LocalDate.of(year, month, it))
        }
        yearSpinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dayList)
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearSpinnerAdapter
        yearSpinner.setSelection(LocalDate.now().dayOfMonth - 1)
        setDataMonth(month - 1, year)
        getData(month - 1, year)
    }

    // Implementations of radio button handlers and data retrieval functions here...
    // Handle the "Weekly" RadioButton selection
    private fun handleWeeklyRadioButton() {
        val dayList = (1..Month.of(month).length(DateUtils().isLeap(year))).map {
            DateUtils().getDate(LocalDate.of(year, month, it))
        }
        yearSpinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dayList)
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearSpinnerAdapter
        yearSpinner.setSelection(LocalDate.now().dayOfMonth - 1)
        setDataMonth(month - 1, year)
        getDailyCategory(DateUtils().getTodaysDate())
        getData(month - 1, year)
    }

    // Handle the "Monthly" RadioButton selection
    private fun handleMonthlyRadioButton() {
        val monthList = (1..12).map { Month.of(it).toString() }
        yearSpinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthList)
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearSpinnerAdapter
        yearSpinner.setSelection(month-1)
        setDataYear(year)
        getMonthlyData(year)
        getMonthlyCategory(month - 1, year)
    }

    // Handle the "Yearly" RadioButton selection
    private fun handleYearlyRadioButton() {
        GlobalScope.launch{
            val yearList = appDatabase.salesDao().getYears().map { it.date }
            val select = "Total"
            yearSpinnerAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf(select) + yearList)
            yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            requireActivity().runOnUiThread {
                getYearlyData()
                yearSpinner.setSelection(0)

                setDataYear(year - 1)
                getYearlyCategory()
                yearSpinner.adapter = yearSpinnerAdapter
            }

        }

    }

    // Handle item selection in the year spinner for weekly view
    private fun handleWeeklySpinnerItemSelection(p0: AdapterView<*>?) {
        if (p0 != null) {
            val day = p0.selectedItem.toString()
            getDailyCategory(day)
        }
    }

    // Handle item selection in the year spinner for monthly view
    private fun handleMonthlySpinnerItemSelection(p0: AdapterView<*>?) {
        if (p0 != null) {
            val month = p0.selectedItemPosition + 1
            getMonthlyCategory(month, year)
        }
    }

    // Handle item selection in the year spinner for yearly view
    private fun handleYearlySpinnerItemSelection(p0: AdapterView<*>?) {
        if (p0 != null) {
            try {
                val year = p0.selectedItem.toString().toInt()
                getYearlyCategory(year)
            }
            catch (e: Exception){
                getYearlyCategory()
            }

        }
    }

    // Implement data retrieval functions (getData, setDataMonth, getMonthlyData, getYearlyData,
    // getDailyCategory, getMonthlyCategory, getYearlyCategory, calculateYearlyTotalSale) here...
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDataMonth(month: Int, year: Int) {
        GlobalScope.launch {


            val s = appDatabase.salesDao().getMonthlySale(month, year)
            val p = appDatabase.salesDao().getMonthlyPurchace(month, requireContext(), year)

            if (s == 0) {
                requireActivity().runOnUiThread {
                    totalSale.text = "₹ 0"
                    totalProfit.text = "₹ 0.00 (0%)"
                }
            } else {
                requireActivity().runOnUiThread {


                    totalSale.text = "₹ " + (dateUtils.formatAmount(s.toDouble()))
                    totalProfit.text =
                        "₹ " + dateUtils.formatAmount((s - p).toDouble()) + "  (" + (100 - (p * 100 / s)).toString() + ")%"
                }
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(month: Int, year: Int) {

        GlobalScope.launch {


            val returnlist = ArrayList<BarData>()
            val profit_list = ArrayList<Int>()
            val today = LocalDate.now()
            for (i in appDatabase.salesDao().getData()) {
                val date = DateUtils().toLocalDate(i.date)!!
                Log.d("TAG", "getData: ${today.dayOfYear}")
                if (month == date.monthValue - 1 && year == date.year) {
                    returnlist.add(
                        BarData(
                            i.daily_sale, "${String.format("%02d", date.dayOfMonth)}-${
                                String.format(
                                    "%02d", date.monthValue
                                )
                            }"
                        )
                    )
                    profit_list.add(i.daily_pur)


                }

            }





            Graphs().setBarChartView(returnlist, barView, requireContext())


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthlyData(year: Int) {
        GlobalScope.launch {


            val returnlist = ArrayList<BarData>()
            val profitList = ArrayList<Int>()
            val today = LocalDate.now()

            for (i in 1..12) {
                val arrayList = appDatabase.salesDao().getMonthlySale(i - 1, year)
                Log.d("TAG", "getData: ${today.dayOfYear}")
                returnlist.add(BarData(arrayList, Month.of(i).toString().slice(0..2)))
                profitList.add(
                    appDatabase.salesDao().getMonthlyPurchace(i - 1, requireContext(), year)
                )

            }


            requireActivity().runOnUiThread {


                Graphs().setBarChartView(returnlist, barView, requireContext())

            }


        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearlyData() {
        GlobalScope.launch {


            val returnlist = ArrayList<BarData>()
            val profitList = ArrayList<Int>()
            val today = LocalDate.now()

            for (i in appDatabase.salesDao().getYears()) {
                val arrayList = appDatabase.salesDao().getYearlySale(i.date.toInt())
                Log.d("TAG", "getData: ${today.dayOfYear}")
                returnlist.add(BarData(arrayList, i.date.toString()))
                profitList.add(
                    appDatabase.salesDao().getYearlyPurchace(i.date.toInt(), requireContext())
                )

            }

            requireActivity().runOnUiThread {
                Graphs().setBarChartView(returnlist, barView, requireContext())

            }


        }


    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDataYear(month: Int) {
        GlobalScope.launch {
            val s = appDatabase.salesDao().getYearlySale(month)
            val p = appDatabase.salesDao().getYearlyPurchace(month, requireContext())

            if (s == 0) {
                requireActivity().runOnUiThread {
                    totalSale.text = "₹ 0"
                    totalProfit.text = "₹ 0.00 (0%)"
                }
            } else {
                requireActivity().runOnUiThread {


                    totalSale.text = "₹ " + (dateUtils.formatAmount(s.toDouble()))
                    totalProfit.text =
                        "₹ " + dateUtils.formatAmount((s - p).toDouble()) + "  (" + (100 - (p * 100 / s)).toString() + ")%"
                }
            }


        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyCategory(day: String) {
        GlobalScope.launch {

            val salesDao = appDatabase.salesDao()

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

            Graphs().setPieChartView(
                dataset, requireView().findViewById<ComposeView>(R.id.piechart), requireContext()
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthlyCategory(month_to: Int, year: Int) {
        GlobalScope.launch {

            val salesDao = appDatabase.salesDao()

            val dataset = ArrayList<Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                if (salesDao.getCategoryDataMonth(
                        "$month_to".format("%02d", month_to), year, i.category_name
                    ) != 0
                ) {
                    dataset.add(
                        Slice(
                            salesDao.getCategoryDataMonth(
                                "$month_to".format("%02d", month_to), year, i.category_name
                            ).toFloat().toInt(), i.category_name.replace("_", " ")
                        )
                    )
                } else {
                    Log.d(
                        "TAG", "getMonthlyCategory: ${"$month".format("%02d", month_to)}, $month_to"
                    )
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

            val salesDao = appDatabase.salesDao()

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
                        "TAG", "getYearlyCategory: ${
                            salesDao.getCategoryDataMonth(
                                String().format(
                                    "%02d", month
                                ), year, i.category_name
                            )
                        }"
                    )
                }
            }
            Graphs().setPieChartView(
                dataset, requireView().findViewById<ComposeView>(R.id.piechart), requireContext()
            )


        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearlyCategory() {
        GlobalScope.launch {

            val salesDao = appDatabase.salesDao()

            val dataset = ArrayList<Slice>()
            val arrayList = salesDao.getCategoryList()
            for (i in arrayList) {
                for (j in salesDao.getYears()) {
                    if (salesDao.getCategoryDataYear(j.date.toInt(), i.category_name) != 0) {
                        dataset.add(
                            Slice(
                                salesDao.getCategoryDataYear(j.date.toInt(), i.category_name)
                                    .toFloat().toInt(), i.category_name.replace("_", " ")
                            )
                        )
                    } else {
                        Log.d(
                            "TAG", "getYearlyCategory: ${
                                salesDao.getCategoryDataMonth(
                                    String().format(
                                        "%02d", month
                                    ), year, i.category_name
                                )
                            }"
                        )
                    }
                }
            }
            requireActivity().runOnUiThread {
                graphs.setPieChartView(dataset, pieView, requireContext())
            }


        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // Handle item selection in the year spinner
        // Implement based on the selected radio button
        when (radioGroup.checkedRadioButtonId) {
            R.id.radioButton1 -> handleWeeklySpinnerItemSelection(p0)
            R.id.radioButton2 -> handleMonthlySpinnerItemSelection(p0)
            R.id.radioButton3 -> handleYearlySpinnerItemSelection(p0)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        // Handle nothing selected in the spinner
    }


}