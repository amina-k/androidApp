package com.strathmore.ads.fruitMartApp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.dtos.OrderItem
import kotlinx.android.synthetic.main.add_order_item_row.view.*


class AddOrderAdapter(
    var orderContext: Context,
    var resource: Int,
    var orderItems: MutableList<OrderItem>
) : ArrayAdapter<OrderItem>(orderContext, resource, orderItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val addOrderView: View
        val layoutInflater: LayoutInflater = LayoutInflater.from(orderContext)

        addOrderView = layoutInflater.inflate(R.layout.add_order_item_row, parent, false)

        val orderItem = orderItems.get(position)


        val itemNameView = addOrderView.itemListProduct
        val itemQuantityView = addOrderView.itemListQuanity
        val itemTotalView = addOrderView.itemListTotal
        itemNameView.text = orderItem.item!!.name
        itemQuantityView.text = "${orderItem.quantity.toString()} @ ${orderItem.item.price}/${orderItem.item.unit}"
        itemTotalView.text = orderItem.total.toString()

        val deleteItem = addOrderView.deleteNewItem
        deleteItem.setOnClickListener {
            orderItems.removeAt(position)
            this.notifyDataSetChanged()
        }
        return addOrderView
    }


}