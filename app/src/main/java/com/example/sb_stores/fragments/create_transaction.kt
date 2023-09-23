package com.example.sb_stores.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R
import com.example.sb_stores.Utils.DateUtils
import com.example.sb_stores.Utils.SpinnerEditText
import com.example.sb_stores.database.AppDatabase
import com.example.sb_stores.database.product_to_sale
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.spec.ECField
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


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
    private var searchResults: List<product_to_sale>? = null


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
        val spinnerEditText = view.findViewById<SpinnerEditText>(R.id.name)
        val searchEditText = spinnerEditText.editText
        val qttyET = view.findViewById<EditText>(R.id.product_qtty)
        val priceET= view.findViewById<EditText>(R.id.product_price)
        val purchasepriceET= view.findViewById<EditText>(R.id.productMRP)
        val today = DateUtils().getTodaysDate()
        date = today
        val spinner = spinnerEditText.spinner
        spinner.visibility = View.GONE






        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Perform the search operation
                GlobalScope.launch{

                    val mydb_ = AppDatabase.getDatabase(requireContext())
                    val searchQuery = s.toString()
                    searchResults = mydb_.apiResponseDao().getSearch(searchQuery)
                    val searchString = ArrayList<String>()
                    searchString.add(0,"select")
                    for (i in searchResults!!){
                        searchString.add(i.name)
                    }




                    val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, searchString)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    requireActivity().runOnUiThread{
                        spinner.setAdapter(adapter)
                        if (searchString.size > 1 && searchEditText.text.isNotEmpty()) {
                            spinner.performClick()
                        }
                        else{
                            spinner.visibility = View.GONE
                        }




                    }



                }


            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
                if (searchResults!!.size < 1){
                    return
                }
                spinner.visibility = View.VISIBLE

//                spinner.clearFocus()
//                searchEditText.hasFocus()
            }
        })





        GlobalScope.launch{
            val mydb_ = AppDatabase.getDatabase(requireContext())
            val categoryList = mydb_.salesDao().getCategoryList()

            categoryList.forEach {
                categories.add(it.category_name)
                Log.d("TAG", "onCreateView: $categories")
            }
            Log.d("TAG", "onCreateView: ")


            val cat_adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            cat_adapter.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item))
            category_spinner!!.adapter = cat_adapter
        }

        view?.findViewById<TextView>(R.id.time_choosen4)?.setText(
            DateUtils().getTodaysDate()
        )

        view.findViewById<ImageButton>(R.id.choose_time2).setOnClickListener {
            showTimePicker()
        }

        view.findViewById<TextView>(R.id.time_choosen4).setOnClickListener {
            showTimePicker()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    val selectedItem = searchResults!![position-1]

                        searchEditText.setText(selectedItem.name)
                        qttyET.setText(selectedItem.qtty.toString())
                    try {
                        priceET.setText(selectedItem.price.toString())
                        purchasepriceET.setText(selectedItem.purchace_price.toString())
                    }
                    catch (e: Exception){
                        Log.d("error", "onItemSelected: ${e.stackTrace}")

                    }

//
                        category_spinner!!.setSelection(categories.indexOf(selectedItem.categoryId))
                    spinner.visibility = View.GONE




                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
                spinner.visibility = View.GONE

            }
        }


        view.findViewById<Button>(R.id.add_task_to_data).setOnClickListener {
            GlobalScope.launch {
                val name = searchEditText.text
                val qtty = qttyET
                val price = priceET
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
                    act.change(R.id.action_create_transaction2_to_transactionHistoryFragment)
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