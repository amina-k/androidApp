package com.strathmore.ads.fruitMartApp.activities

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.adapters.SingleOrderAdapter
import com.strathmore.ads.fruitMartApp.dtos.Orders
import kotlinx.android.synthetic.main.activity_single_order.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SingleOrderActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_order)

        val order = intent.getSerializableExtra("ORDER") as Orders
//        val order = orderBundle[0]
        val orderItemsList = order.orderItems!!
        val listView: ListView = singleOrderListView

        val singleOrderAdapter = SingleOrderAdapter(this, R.layout.single_order_item_row, orderItemsList, order)

        listView.adapter = singleOrderAdapter


        val formatter = DateTimeFormatter.ofPattern(
            "dd MMM, yyyy hh:mm a"
        )
        val displayDate = LocalDateTime.parse(order.orderDate?.substringBefore(".")).format(formatter)

        singleOrderTotal.text = "Order Total: Sh ${order.orderTotal!!}"
        singleOrderCashierName.text = "Cashier: ${order.cashierName!!}"
        singleOrderDate.text = displayDate
        singleAmountPaid.text = "Amount Paid: Sh ${order.amountPaid!!}"
        singleBalanceDue.text = "Balance Due: Sh ${order.amountPaid - order.orderTotal}"


    }
}
