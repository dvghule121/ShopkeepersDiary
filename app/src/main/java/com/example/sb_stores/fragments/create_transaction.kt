package com.example.sb_stores.fragments

import android.R.string
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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
import kotlin.collections.ArrayList


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
    private var categories = ArrayList<String>()
    private var category_spinner :Spinner? = null


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
        val view =  inflater.inflate(R.layout.fragment_create_transaction, container, false)
        val act = activity as MainActivity
        calendar = Calendar.getInstance()
        category_spinner = view.findViewById<Spinner>(R.id.category)

        GlobalScope.launch{
            val mydb_ = AppDatabase.getDatabase(requireContext())
            val categoryList = mydb_.salesDao().getCategoryList()

            categoryList.forEach {
                categories.add(it.category_name)
                Log.d("TAG", "onCreateView: $categories")
            }
            Log.d("TAG", "onCreateView: ")


            val cat_adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            cat_adapter.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
            category_spinner!!.adapter = cat_adapter
        }

        view.findViewById<ImageButton>(R.id.choose_time).setOnClickListener {
            showTimePicker()
        }

        view.findViewById<TextView>(R.id.time_choosen4).setOnClickListener {
            showTimePicker()
        }



        view.findViewById<Button>(R.id.add_task_to_data).setOnClickListener {
            GlobalScope.launch {
                val name = view.findViewById<EditText>(R.id.name).text
                val qtty = view.findViewById<EditText>(R.id.product_qtty)
                val price = view.findViewById<EditText>(R.id.product_price)
                var category = ""
                if (categories.isNotEmpty()){
                    category = category_spinner!!.selectedItem.toString()
                }

                val mrp = view.findViewById<EditText>(R.id.productMRP).text.toString()
                val date = date
                if (name.toString() != "" && price.text.toString() != "" && mrp != "" && date != null && category != "" ){
                val mydb_ = AppDatabase.getDatabase(requireContext())
                val mydb = mydb_.apiResponseDao()







                val id = mydb.insertData(
                    product_to_sale(
                        calendar!!.timeInMillis.toInt(),
                        name.toString(),
                        mrp.toInt(),
                        price.text.toString().toInt(),
                        qtty.text.toString().toFloat(),
                        date.toString(),
                        calendar!!.time.toString(),
                        category.toString()
                    )

                )


                    mydb_.salesDao().updateData(date.toString(), (qtty.text.toString().toFloat() * price.text.toString().toInt()).toInt(),(mrp.toInt() * qtty.text.toString().toFloat()).toInt())
                    mydb_.salesDao()
                        .updateCategoryData(date.toString(), (qtty.text.toString().toFloat() * price.text.toString().toInt()).toInt(), category.toString())
                    act.change(transaction_history())
                }
                else{
                    act.runOnUiThread{
                        Toast.makeText(requireActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
                    }

                }

            }





        }
        val addCategoryDialog =
            Dialog(requireContext(), androidx.appcompat.R.style.AlertDialog_AppCompat_Light)
        addCategoryDialog.setContentView(R.layout.add_category_dialog)

        view.findViewById<Button>(R.id.add_category).setOnClickListener {
            addCategoryDialog.show()


        }

        addCategoryDialog.findViewById<Button>(R.id.add_category).setOnClickListener {
            kotlinx.coroutines.GlobalScope.launch {
                var category =
                    addCategoryDialog.findViewById<android.widget.EditText>(com.example.sb_stores.R.id.category_name).text.toString()
                if (category != ""){
                    category = category.replace("\\s".toRegex(), "_")
                    val mydb_ = com.example.sb_stores.database.AppDatabase.getDatabase(requireContext())
                    mydb_.salesDao().addCategoryData(date.toString(), category.toString())
                    val temp=ArrayList<String>()

                    for (i in mydb_.salesDao().getCategoryList()){
                        temp.add(i.category_name)
                    }

                    setData(temp)
                    addCategoryDialog.cancel()
                }
                else {
                    requireActivity().runOnUiThread{
                        Toast.makeText(requireActivity(), "Please fill category first", Toast.LENGTH_SHORT).show()

                    }
                                 }




            }





        }

        return view
    }
    fun setData(data: ArrayList<String>){
        requireActivity().runOnUiThread{
            val cat_adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
            cat_adapter.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
            category_spinner!!.adapter = cat_adapter
        }



    }


    fun showTimePicker() {

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


    }
}