package com.example.sb_stores


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView


class sampleAdapter(val context: Context, val item: Int, val view :Int = R.layout.goal_card): RecyclerView.Adapter<sampleAdapterViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): sampleAdapterViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(view, parent, false)
        return sampleAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: sampleAdapterViewHolder, position: Int) {

//        holder.itemView.setOnClickListener {
//            val context_a = context as MainActivity
//            context_a.change(goal_details())
//        }
    }

    override fun getItemCount(): Int {
        return item
    }
}

class sampleAdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

}
