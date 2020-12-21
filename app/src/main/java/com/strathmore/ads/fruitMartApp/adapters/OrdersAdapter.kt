package com.strathmore.ads.fruitMartApp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.dtos.Orders
import kotlinx.android.synthetic.main.order_row.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class OrdersAdapter(
    var orderContext: Context,
    var resource: Int,
    var orders: List<Orders>
) : ArrayAdapter<Orders>(orderContext, resource, orders) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val orderView: View
        val layoutInflater: LayoutInflater = LayoutInflater.from(orderContext)


        orderView = layoutInflater.inflate(R.layout.order_row, parent, false)

        val order = orders.get(position)
        val dateFormatter = DateTimeFormatter.ofPattern(
            "dd MMM, yyyy"
        )
        val timeFormatter = DateTimeFormatter.ofPattern(
            "hh:mm a"
        )
        val displayDate = LocalDateTime.parse(order.orderDate!!.substringBefore(".")).format(dateFormatter)
        val displayTime = LocalDateTime.parse(order.orderDate!!.substringBefore(".")).format(timeFormatter)

        val cashierView = orderView.cashierName
        val totalView = orderView.orderTotal
        val dateView = orderView.orderDate
        val timeView = orderView.orderTime

        cashierView.text = order.cashierName
        dateView.text = displayDate
        totalView.text = "Ksh: ${order.orderTotal.toString()}"
        timeView.text = displayTime

        return orderView
    }


}