package com.strathmore.ads.fruitMartApp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.dtos.OrderItem
import com.strathmore.ads.fruitMartApp.dtos.Orders
import kotlinx.android.synthetic.main.single_order_item_row.view.*


class SingleOrderAdapter(
    var orderContext: Context,
    var resource: Int,
    var orderItems: List<OrderItem>,
    var order: Orders
) : ArrayAdapter<OrderItem>(orderContext, resource, orderItems) {
    private var ordersList: MutableList<OrderItem> = mutableListOf()
    private var orderTotal: Double = 0.0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val singleOrderView: View
        val layoutInflater: LayoutInflater = LayoutInflater.from(orderContext)

        singleOrderView = layoutInflater.inflate(R.layout.single_order_item_row, parent, false)

        val orderItem = orderItems.get(position)


        val singleProductNameView = singleOrderView.singleProduct
        val singleItemQuantityView = singleOrderView.singleQuantity
        val singleItemTotalView = singleOrderView.singleTotal
        singleProductNameView.text = orderItem.item!!.name
        singleItemQuantityView.text = orderItem.quantity.toString()
        singleItemTotalView.text = "KSh: ${orderItem.total.toString()}"


        return singleOrderView
    }

}