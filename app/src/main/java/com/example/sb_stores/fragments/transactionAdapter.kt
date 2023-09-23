package com.example.sb_stores.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation

import androidx.recyclerview.widget.RecyclerView
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R
import com.example.sb_stores.database.product_to_sale


class  transactionAdapter(val activity:Activity, val view :Int = R.layout.transaction_card): RecyclerView.Adapter< transactionAdapterViewHolder>() {

    var productList = emptyList<product_to_sale>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):  transactionAdapterViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(view, parent, false)
        return  transactionAdapterViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder:  transactionAdapterViewHolder, position: Int) {
        val product = productList[position]
        holder.name.text = product.name
        holder.total.text ="₹ " + (product.price * product.qtty).toString()
        holder.price.text ="₹ " + (product.price ).toString()
        holder.mrp.text = "M.R.P: ₹ " + (product.purchace_price).toString()
        holder.qtty.text = "Quantity: " + (product.qtty).toString()
        holder.date.text = product.time
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", product.id)
            val ftc = transaction_details()
            ftc.arguments = bundle
            val act = activity as MainActivity
            act.change(R.id.action_transactionHistoryFragment_to_transaction_details, bundle)
        }

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun setData(list: List<product_to_sale>){
        this.productList = list
        notifyDataSetChanged()
    }
}

class  transactionAdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    val name = itemView.findViewById<TextView>(R.id.product_name)
    val total = itemView.findViewById<TextView>(R.id.total_amount)
    val price = itemView.findViewById<TextView>(R.id.sale_price)
    val mrp = itemView.findViewById<TextView>(R.id.mrp)
    val date = itemView.findViewById<TextView>(R.id.date)
    val qtty = itemView.findViewById<TextView>(R.id.qtty)

}
