package com.example.sb_stores.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.product_to_sale
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [create_transaction.newInstance] factory method to
 * create an instance of this fragment.
 */
class create_transaction :  Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var picker: MaterialTimePicker? = null
    private var calendar : Calendar? = null
    private var time : Time? = null
    private var date:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_create_transaction, container, false)
        val act = activity as MainActivity
        calendar = Calendar.getInstance()


        view.findViewById<ImageButton>(R.id.choose_time).setOnClickListener {
            showTimePicker()
        }


        view.findViewById<Button>(R.id.add_task_to_data).setOnClickListener {
            GlobalScope.launch {
            val mydb_ = AppDatabase.getDatabase(requireContext())
            val mydb = mydb_.apiResponseDao()

            val name = view.findViewById<EditText>(R.id.name).text.toString()
            val qtty = view.findViewById<EditText>(R.id.product_qtty).text.toString().toFloat()
            val price = view.findViewById<EditText>(R.id.product_price).text.toString().toInt()
            val category = view.findViewById<Spinner>(R.id.category).selectedItem.toString()
            val mrp = view.findViewById<EditText>(R.id.productMRP).text.toString().toInt()
            val date = date


            val id = mydb.insertData(product_to_sale(0, name, mrp,price, qtty,date.toString(), calendar!!.time.toString(),category))
                mydb_.salesDao().updateData(date.toString(), (qtty * price).toInt() )
                mydb_.salesDao().updateCategoryData(date.toString(),(qtty * price).toInt(), category )
            }

            act.change(dashboard())



        }

        return view
    }


    fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        val currentYear: Int = calendar!!.get(Calendar.YEAR)
        val currentMonth: Int = calendar!!.get(Calendar.MONTH)
        val currentDay: Int = calendar!!.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { datePicker, yearOfDay, MonthOfDay, dayOfDay ->

                date = "${
                    String.format(
                        "%02d",
                        dayOfDay
                    )
                }-${String.format("%02d", MonthOfDay + 1)}-$yearOfDay"

                view?.findViewById<TextView>(R.id.time_choosen4)?.setText(
                    date
                )
                calendar!!.set(Calendar.YEAR, yearOfDay)
                calendar!!.set(Calendar.MONTH, MonthOfDay)
                calendar!!.set(Calendar.DATE, dayOfDay)
            },
            currentYear,
            currentMonth,
            currentDay,
        )
        datePickerDialog.show()

        picker!!.show(requireActivity().supportFragmentManager, "foxandroid")
        picker!!.addOnPositiveButtonClickListener(View.OnClickListener {

            view?.findViewById<TextView>(R.id.time_choosen)!!.text  = "${picker!!.hour}:${picker!!.minute}"
            time = Time(picker!!.hour,picker!!.minute,0)


            calendar!!.set(Calendar.HOUR_OF_DAY, picker!!.getHour())
            calendar!!.set(Calendar.MINUTE, picker!!.getMinute())
            calendar!!.set(Calendar.SECOND, 0)
            calendar!!.set(Calendar.MILLISECOND, 0)

        })


    }
}