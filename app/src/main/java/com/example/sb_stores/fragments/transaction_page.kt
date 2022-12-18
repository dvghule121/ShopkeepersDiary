package com.example.sb_stores.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sb_stores.R
import com.example.sb_stores.database.AppDatabase

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [transaction_page.newInstance] factory method to
 * create an instance of this fragment.
 */
class transaction_page : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

        val bundle = arguments
        val date = bundle!!.get("date")

        val view =  inflater.inflate(R.layout.fragment_transaction_page, container, false)
        val habit_view = view.findViewById<RecyclerView>(R.id.habit_view)
        habit_view.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        GlobalScope.launch {

        val l = AppDatabase.getDatabase(requireActivity().applicationContext).apiResponseDao().getDataOfDate(
            date as String
        )


        val adapter = transactionAdapter(requireActivity())
        adapter.setData(l)
        habit_view.adapter = adapter



//        view.findViewById<FloatingActionButton>(R.id.add_habbit).setOnClickListener {
//            val act = activity as MainActivity
//            act.change(add_habit())
//        }
        }

        return view
    }


}