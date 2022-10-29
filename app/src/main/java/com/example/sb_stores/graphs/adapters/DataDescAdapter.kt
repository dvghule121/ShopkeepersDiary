package com.example.samplechart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sb_stores.R
import com.example.sb_stores.graphs.BarItem


class DataDescAdapter (var BarDataSet: List<BarItem>, var ProfitData: List<Int>): RecyclerView.Adapter<DataDescAdapterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataDescAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.data_desc_item, parent,  false)
        return DataDescAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataDescAdapterViewHolder, position: Int) {
        val p = ProfitData[position]
        val barItem = BarDataSet[position]
        val s = barItem.value
        holder.label.text = barItem.label
        holder.bar.text = barItem.value.toString()
        holder.profit.text = (s!! - p).toString() + if (barItem.value != 0 )" (${(s-p)*100/s}%)" else " (0%)"

    }

    override fun getItemCount(): Int {
        return  BarDataSet.size
    }

    fun setData( BarDataSet: ArrayList<BarItem>, ProfitData: List<Int>){
        this.BarDataSet = BarDataSet
        this.ProfitData = ProfitData
        notifyDataSetChanged()
    }

}

class DataDescAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val bar = itemView.findViewById<TextView>(R.id.dataValue)
    val label = itemView.findViewById<TextView>(R.id.dataLabel)
    val profit = itemView.findViewById<TextView>(R.id.profit_val)
}
