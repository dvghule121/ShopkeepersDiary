package com.example.sb_stores.fragments


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.samplechart.SimplePieChart.SimplePieChart
import com.example.sb_stores.DateUtils
import com.example.sb_stores.R
import com.example.sb_stores.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random


class dashboard : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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

        val awesomePieChart: SimplePieChart =
            view.findViewById<View>(R.id.piechart) as SimplePieChart

//        val dataset = getSlicesData("{\"errorMessage\":null,\"payload\":{\"data\":[{\"value\":10,\"name\":\"Maintenance\"},{\"value\":2,\"name\":\"Housekeeping\"},{\"value\":1,\"name\":\"Maintenance\"},{\"value\":14,\"name\":\"Updated category\"},{\"value\":10,\"name\":\"Maintenance\"}]},\"status\":\"SUCCESS\",\"requestId\":null,\"detail\":null}")

        GlobalScope.launch {

            val database = AppDatabase.getDatabase(requireContext()).salesDao()
            val sum = database.getDataOfDate(DateUtils().getTodaysDate())
            var amount = 0
            if (sum.isNotEmpty()) amount = sum[0].daily_sale
            view.findViewById<TextView>(R.id.sale_amount).text = "₹ $amount"


            val dataset_pie =
                AppDatabase.getDatabase(requireActivity().applicationContext).salesDao()

            val dataset = ArrayList<SimplePieChart.Slice>()
            val arrayList = resources.getStringArray(R.array.categories)
            for (i in arrayList) {
                if (dataset_pie.getCategoryData(DateUtils().getTodaysDate(), i) != 0) {
                    dataset.add(
                        SimplePieChart.Slice(
                            (Math.random() * 16777215).toInt() or (0xFF shl 24),
                            dataset_pie.getCategoryData(DateUtils().getTodaysDate(), i).toFloat(),
                            i
                        )
                    )
                }
            }
            addDataset(awesomePieChart, dataset as ArrayList<SimplePieChart.Slice>)
        }
        return view
    }

    fun addDataset(pieChart: SimplePieChart, dataset: ArrayList<SimplePieChart.Slice>) {
        for (i in dataset) {
            pieChart.addSlice(i)
        }
    }


}