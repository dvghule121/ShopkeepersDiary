package com.example.sb_stores.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.product_to_sale
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class transaction_details : Fragment() {
    private var picker: MaterialTimePicker? = null
    private var calendar : Calendar? = null
    private var time : Time? = null
    private var date:String? = null
    private var categories = ArrayList<String>()
    private var category_spinner :Spinner? = null
    private lateinit var mydb_:AppDatabase
    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaction_details, container, false)

        val act = activity as MainActivity
        calendar = Calendar.getInstance()
        category_spinner = view.findViewById<Spinner>(R.id.category)


        id  = requireArguments().getInt("id")
        GlobalScope.launch{
            mydb_ = AppDatabase.getDatabase(requireContext())
            val categoryList = mydb_.salesDao().getCategoryList()
            val productToSale = mydb_.apiResponseDao().getDataById(id!!)[0]

            val name = view.findViewById<EditText>(R.id.name)
            val qtty = view.findViewById<EditText>(R.id.product_qtty)
            val price = view.findViewById<EditText>(R.id.product_price)
            val category = category_spinner!!.selectedItem
            val mrp = view.findViewById<EditText>(R.id.productMRP)
            date = productToSale.date
            view.findViewById<TextView>(R.id.time_choosen4).text = date
            name.setText(productToSale.name.toString())
            price.setText(productToSale.price.toString())
            mrp.setText(productToSale.purchace_price.toString())
            qtty.setText(productToSale.qtty.toInt().toString())





            categories.add(productToSale.categoryId)

            category_spinner!!.isEnabled = false
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
                val id  = requireArguments().getInt("id")
                val productToSale = mydb_.apiResponseDao().getDataById(id)[0]

                val name = view.findViewById<EditText>(R.id.name)
                val qtty = view.findViewById<EditText>(R.id.product_qtty)
                val price = view.findViewById<EditText>(R.id.product_price)
                val category = productToSale.categoryId
                val mrp = view.findViewById<EditText>(R.id.productMRP)
                val date = date

                if (name.text.toString() != "" && price.text.toString() != "" && mrp.text.toString() != "" && date != null && category != null){
                    val mydb = mydb_.apiResponseDao()

                    mydb.updateData(
                        product_to_sale(
                            productToSale.id,
                            name.text.toString(),
                            mrp.text.toString().toInt(),
                            price.text.toString().toInt(),
                            qtty.text.toString().toFloat(),
                            date.toString(),
                            calendar!!.time.toString(),
                            category.toString()
                        )

                    )
                    val s = (qtty.text.toString().toFloat() * price.text.toString().toInt()).toInt()
                    val p = (mrp.text.toString().toInt() * qtty.text.toString().toFloat()).toInt()



                    if (productToSale.date == date){
                        mydb_.salesDao().updateData(date.toString(), -(productToSale.price* productToSale.qtty).toInt() , -(productToSale.purchace_price* productToSale.qtty).toInt())
                        mydb_.salesDao().updateData(date.toString(), s , p)
                        mydb_.salesDao()
                            .updateCategoryData(date.toString(), s- (productToSale.price* productToSale.qtty).toInt() , category.toString())

                    }else{
                        mydb_.salesDao().updateData(productToSale.date, -(productToSale.price* productToSale.qtty).toInt() , -(productToSale.purchace_price* productToSale.qtty).toInt())
                        mydb_.salesDao().updateData(date, s , p)
                        mydb_.salesDao()
                            .updateCategoryData(date.toString(), s , category.toString())
                        mydb_.salesDao()
                            .updateCategoryData(productToSale.date, -(productToSale.price* productToSale.qtty).toInt() , category.toString())

                    }
                    act.change(transaction_history())
                }
                else{
                    act.runOnUiThread{
                        Toast.makeText(requireActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
                    }

                }

            }





        }

        view.findViewById<Button>(R.id.delete).setOnClickListener {
            GlobalScope.launch {
                val p = mydb_.apiResponseDao().getDataById(id!!) [0]
                mydb_.apiResponseDao().delete(id!!)
                mydb_.salesDao().updateData(p.date, -(p.price*p.qtty).toInt(), -(p.purchace_price*p.qtty).toInt())
                mydb_.salesDao().updateCategoryData(p.date, -(p.qtty*p.price).toInt(),p.categoryId)
                act.change(transaction_history())
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
                if (category != "") {
                    category = category.replace("\\s".toRegex(), "_")
                    val mydb_ =
                        com.example.sb_stores.database.AppDatabase.getDatabase(requireContext())
                    mydb_.salesDao().addCategoryData(date.toString(), category.toString())
                    val temp = ArrayList<String>()

                    for (i in mydb_.salesDao().getCategoryList()) {
                        temp.add(i.category_name)
                    }
                    setData(temp)
                    addCategoryDialog.cancel()
                }else{
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Please fill text field.", Toast.LENGTH_SHORT).show()
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