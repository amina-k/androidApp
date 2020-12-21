package com.strathmore.ads.fruitMartApp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.activities.DashboardActivity
import com.strathmore.ads.fruitMartApp.activities.SingleOrderActivity
import com.strathmore.ads.fruitMartApp.activities.mapper
import com.strathmore.ads.fruitMartApp.adapters.OrdersAdapter
import com.strathmore.ads.fruitMartApp.dtos.Orders
import kotlinx.android.synthetic.main.fragment_items.itemProgressBar
import kotlinx.android.synthetic.main.fragment_orders.*


class OrdersFragment : Fragment() {
    val ordersList: MutableList<Orders> = mutableListOf()
    var orderAdapter: OrdersAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)

    }

    override fun onViewCreated(pricesView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(pricesView, savedInstanceState)

        val context = context as DashboardActivity

        val listView: ListView = context.ordersListView

        orderAdapter = OrdersAdapter(context, R.layout.item_row, ordersList)

        listView.adapter = orderAdapter

        loadOrders()


        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            val selectedOrder: Orders = ordersList[position]

            val intent = Intent(context, SingleOrderActivity::class.java)
            intent.putExtra("ORDER", selectedOrder)
            startActivity(intent)
        }

    }


    companion object {

        fun newInstance(): OrdersFragment = OrdersFragment()
    }


    private fun loadOrders() {

        Fuel.get("https://09c5d121a16f.ngrok.io/fetchAllOrders")
            .response { _, _, result ->
                val (payload, error) = result

                val items: Map<String, Any?> = mapper.readValue(payload!!)
                val data = items["data"] as Map<String, Map<String, String>>

                data.forEach {
                    ordersList.add(
                        mapper.convertValue(
                            it.value,
                            object : TypeReference<Orders>() {})
                    )
                }

                orderAdapter!!.notifyDataSetChanged()

                itemProgressBar.visibility = View.GONE
            }


    }

}

