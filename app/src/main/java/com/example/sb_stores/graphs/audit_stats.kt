package com.example.sb_stores.graphs


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samplechart.adapters.BarDataAdapter
import com.example.samplechart.adapters.DataDescAdapter
import com.example.sb_stores.DateUtils
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.Sales
import com.example.sb_stores.fragments.transaction_history
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [audit_stats.newInstance] factory method to
 * create an instance of this fragment.
 */

@RequiresApi(Build.VERSION_CODES.O)
private var month = LocalDate.now().month.value

@RequiresApi(Build.VERSION_CODES.O)
private var year = LocalDate.now().year

class audit_stats : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var month_spinner: Spinner? = null
    private var filterSpinner: Spinner? = null
    private var BarViewAdapter: BarDataAdapter? = null
    private var DataDescViewAdapter: DataDescAdapter? = null
    private var filter_adapter: ArrayAdapter<String>? = null
    private var db: List<Sales>? = null
    private lateinit var AppDatabase: AppDatabase
    private lateinit var t_m: TextView
    private lateinit var t_75: TextView
    private lateinit var t_50: TextView
    private lateinit var t_25: TextView
    private var max: Int = 0


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
        val view = inflater.inflate(R.layout.fragment_audit_stats, container, false)

        val barView = view.findViewById<RecyclerView>(R.id.bar_view)
        val dataDescView = view.findViewById<RecyclerView>(R.id.data_desc_view)

        barView.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        dataDescView.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        month_spinner = view.findViewById<Spinner>(R.id.month_spinner)
        filterSpinner = view.findViewById(R.id.spinner_filter)
        BarViewAdapter = BarDataAdapter(emptyList())
        DataDescViewAdapter = DataDescAdapter(emptyList(), emptyList())
        barView.adapter = BarViewAdapter
        dataDescView.adapter = DataDescViewAdapter
        t_m = view.findViewById<TextView>(R.id.bar_y_max)
        t_75 = view.findViewById<TextView>(R.id.bar_Y_75)
        t_50 = view.findViewById<TextView>(R.id.bar_Y_50)
        t_25 = view.findViewById<TextView>(R.id.bar_Y_25)
        month_spinner!!.onItemSelectedListener = this
        filterSpinner!!.onItemSelectedListener = this

        GlobalScope.launch {

            AppDatabase = com.example.sb_stores.database.AppDatabase.getDatabase(requireContext())
            getMonthlyData(year)
            max = 500000

            requireActivity().runOnUiThread {
                month_spinner!!.onItemSelectedListener = this@audit_stats
                if (max != 0) {
                    t_25.text = ((max) / 4).toString()
                    t_m.text = max.toString()
                    t_75.text = ((max * 3) / 4).toString()
                    t_50.text = ((max) / 2).toString()
                }
            }

        }


        view.findViewById<Button>(R.id.check_more).setOnClickListener {
            val act = requireActivity() as MainActivity
            act.change(transaction_history())
        }




        return view
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(month: Int, year: Int) {

        GlobalScope.launch {


            val returnlist = ArrayList<BarItem>()
            val profit_list = ArrayList<Int>()
            val today = LocalDate.now()
            for (i in AppDatabase.salesDao().getData()) {
                val date = DateUtils().toLocalDate(i.date)!!
                Log.d("TAG", "getData: ${today.dayOfYear}")
                if (month == date.monthValue - 1 && year == date.year) {
                    returnlist.add(
                        BarItem(
                            (i.daily_sale).toInt(),
                            "${String.format("%02d", date.dayOfMonth)}-${
                                kotlin.String.format(
                                    "%02d",
                                    date.monthValue
                                )
                            }-${year}"
                        )
                    )
                    profit_list.add(i.daily_pur)

            }

        }


        max = 20000
            if (returnlist.maxOf { it.value!! } > max){
                max = 5* max
            }
        requireActivity().runOnUiThread {
            month_spinner!!.onItemSelectedListener = this@audit_stats
            if (max != 0) {
                t_25.text = ((max) / 4).toString()
                t_m.text = max.toString()
                t_75.text = ((max * 3) / 4).toString()
                t_50.text = ((max) / 2).toString()
            }


            BarViewAdapter!!.setData(returnlist,max)
            DataDescViewAdapter!!.setData(returnlist,profit_list)
        }


    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getMonthlyData(year: Int) {
    GlobalScope.launch {

        max = 100000



        val returnlist = ArrayList<BarItem>()
        val profitList = ArrayList<Int>()
        val today = LocalDate.now()

        for (i in 1..12) {
            val arrayList =
                AppDatabase.salesDao().getMonthlySale(i - 1, year)
            Log.d("TAG", "getData: ${today.dayOfYear}")
            returnlist.add(BarItem(arrayList, Month.of(i).toString().slice(0..2)))
            profitList.add(AppDatabase.salesDao().getMonthlyPurchace(i-1,requireContext(),year))

        }

        if (returnlist.maxOf { it.value!! } > max){
            max = 5 * max
        }

        requireActivity().runOnUiThread {
            month_spinner!!.onItemSelectedListener = this@audit_stats
            if (max != 0) {
                t_25.text = ((max) / 4).toString()
                t_m.text = max.toString()
                t_75.text = ((max * 3) / 4).toString()
                t_50.text = ((max) / 2).toString()
            }
        }
        requireActivity().runOnUiThread {
            BarViewAdapter!!.setData(returnlist,max)
            DataDescViewAdapter!!.setData(returnlist,profitList)
        }


    }


}

@RequiresApi(Build.VERSION_CODES.O)
fun getYearlyData() {
    GlobalScope.launch {

        max = 2000000
        requireActivity().runOnUiThread {
            month_spinner!!.onItemSelectedListener = this@audit_stats
            if (max != 0) {
                t_25.text = ((max) / 4).toString()
                t_m.text = max.toString()
                t_75.text = ((max * 3) / 4).toString()
                t_50.text = ((max) / 2).toString()
            }
        }


        val returnlist = ArrayList<BarItem>()
        val profitList = ArrayList<Int>()
        val today = LocalDate.now()

        for (i in AppDatabase.salesDao().getYears()) {
            val arrayList =
                AppDatabase.salesDao().getYearlySale(i.date.toInt())
            Log.d("TAG", "getData: ${today.dayOfYear}")
            returnlist.add(BarItem(arrayList, i.date.toString()))
            profitList.add(AppDatabase.salesDao().getYearlyPurchace(i.date.toInt(), requireContext()))

        }

        requireActivity().runOnUiThread {
            BarViewAdapter!!.setData(returnlist, max)
            DataDescViewAdapter!!.setData(returnlist,profitList)
        }


    }


}

@RequiresApi(Build.VERSION_CODES.O)
fun filter(choice: String) {
    when (choice) {
        "Yearly" -> getMonthlyData(LocalDate.now().year)
        "Monthly" -> getData(Date().month, LocalDate.now().year)
        else -> getData(1, LocalDate.now().year)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)


    if (parent.id == R.id.month_spinner) {
        if (filterSpinner!!.selectedItem == "Monthly") year =
            parent.selectedItem.toString().toInt()
        else {
            month = pos
        }
    } else {
        if (parent.selectedItem == "Monthly" ) {
            GlobalScope.launch {
                val years = AppDatabase.salesDao().getYears()
                val temp = ArrayList<String>()
                if (years.isNotEmpty()) {
                    for (i in years) {
                        temp.add(i.date)
                    }
                } else {
                    temp.add(LocalDate.now().year.toString())
                }
                requireActivity().runOnUiThread {

                    filter_adapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, temp)
                    filter_adapter!!.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
                    month_spinner!!.adapter = filter_adapter
                }
            }
            getMonthlyData(year)
        }
        else if (parent.selectedItem == "Yearly"){
            filter_adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, emptyList())
            filter_adapter!!.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
            month_spinner!!.adapter = filter_adapter
        }
        else {
            val a = ArrayList<String>()
            for (i in 1..12) {
                a.add(Month.of(i).toString())
            }


            filter_adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, a)
            filter_adapter!!.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
            month_spinner!!.adapter = filter_adapter

            getData(month, year)
        }


    }
    if (filterSpinner!!.selectedItem == "Daily") {
        getData(month, year)
        month_spinner!!.setSelection(Date().month)
    } else if (filterSpinner!!.selectedItem == "Yearly") {
        getYearlyData()
    } else {
        getMonthlyData(year)
    }


}

override fun onNothingSelected(parent: AdapterView<*>) {
    // Another interface callback
}


}

