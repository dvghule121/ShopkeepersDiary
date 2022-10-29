package com.example.samplechart.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sb_stores.R
import com.example.sb_stores.graphs.BarItem


class BarDataAdapter(var BarDataSet : List<BarItem>): RecyclerView.Adapter<BarDataAdapterViewHolder>() {

    var max = 2000000

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarDataAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bar_item, parent,  false)
        return BarDataAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarDataAdapterViewHolder, position: Int) {
        val barItem = BarDataSet[position]
        holder.bar.max = max
        holder.bar.progress = barItem.value!!
        holder.label.text = barItem.label


    }

    override fun getItemCount(): Int {
        return  BarDataSet.size
    }

    fun setData( BarDataSet: ArrayList<BarItem>, max:Int=100000){
        this.BarDataSet = BarDataSet
        this.max = max
        notifyDataSetChanged()
    }
}

class BarDataAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val bar = itemView.findViewById<ProgressBar>(R.id.barItem)
    val label = itemView.findViewById<TextView>(R.id.barLabel)
}
