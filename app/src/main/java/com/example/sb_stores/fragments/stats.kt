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
import com.example.sb_stores.graphs.audit_stats
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

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


    private lateinit var monthlyprofit: TextView
    private lateinit var monthlysale: TextView
    private lateinit var yearlysale: TextView
    private lateinit var yearlyprofit: TextView
    private var month : Int = LocalDate.now().month.value
    private var year: Int = LocalDate.now().year
    lateinit var year_spinner:Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        monthlysale = view.findViewById<TextView>(R.id.sale_amount_month)
        monthlyprofit = view.findViewById<TextView>(R.id.profit_month)
        yearlysale = view.findViewById<TextView>(R.id.sale_amount_year)
        yearlyprofit = view.findViewById(R.id.profit_year)


        val month_spinner = view.findViewById<Spinner>(R.id.spinner_month)
        year_spinner = view.findViewById<Spinner>(R.id.spinner_year)

        month_spinner.onItemSelectedListener = this
        year_spinner.onItemSelectedListener = this

        month_spinner.setSelection(month-1)
        setDataMonth(month-1, year)
        setDataYear(year)

        GlobalScope.launch {
            val years = AppDatabase.getDatabase(requireContext()).salesDao().getYears()
            val temp = ArrayList<String>()
            if (years.isNotEmpty()){
                for (i in years){
                    temp.add(i.date)

                }}
            else{
                temp.add(LocalDate.now().year.toString())
            }
            requireActivity().runOnUiThread{

                val cat_adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, temp)
                cat_adapter.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
                year_spinner.adapter = cat_adapter
            }
        }




        childFragmentManager.beginTransaction().add(R.id.fragmentContainerView, audit_stats())
            .commit()
        return view
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDataMonth(month: Int, year: Int) {
        GlobalScope.launch {


            val s = AppDatabase.getDatabase(requireContext()).salesDao().getMonthlySale(month, year)
            val p = AppDatabase.getDatabase(requireContext()).salesDao()
                .getMonthlyPurchace(month, requireContext(),year)

            if (s == 0 ) {
                requireActivity().runOnUiThread {
                    monthlysale.text = "₹ 0"
                    monthlyprofit.text = "Profit ₹ 0.00 (0%)"
                }
            } else {
                requireActivity().runOnUiThread {


                    monthlysale.text = "₹ " + (s).toString()
                    monthlyprofit.text =
                        "Profit ₹ " + (s - p).toString() + "  (" + (100-(p * 100 / s)).toString() + ")%"
                }
            }


        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setDataYear(month: Int) {
        GlobalScope.launch {
            val s = AppDatabase.getDatabase(requireContext()).salesDao().getYearlySale(month)
            val p = AppDatabase.getDatabase(requireContext()).salesDao()
                .getYearlyPurchace(month, requireContext())

            if (s == 0 ) {
                requireActivity().runOnUiThread {
                    yearlysale.text = "₹ 0"
                    yearlyprofit.text = "Profit ₹ 0.00 (0%)"
                }
            }
            else {
                requireActivity().runOnUiThread {



                    yearlysale.text = "₹ " + (s).toString()
                    yearlyprofit.text =
                        "Profit ₹ " + (s - p).toString() + "  (" + (100-(p * 100 / s)).toString() + ")%"
                }
            }



        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0!!.id == R.id.spinner_month) {

            month = p2
        }else {
            year = p0.selectedItem.toString().toInt()
            Log.d("TAG", "onItemSelected: Item seell $year")
        }
        setDataYear(year)
        setDataMonth(month, year)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


}