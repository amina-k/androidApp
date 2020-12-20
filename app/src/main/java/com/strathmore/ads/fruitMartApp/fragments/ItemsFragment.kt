package com.strathmore.ads.fruitMartApp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.activities.DashboardActivity
import com.strathmore.ads.fruitMartApp.activities.mapper
import com.strathmore.ads.fruitMartApp.adapters.ItemsAdapter
import com.strathmore.ads.fruitMartApp.dtos.Item
import kotlinx.android.synthetic.main.fragment_items.*


class ItemsFragment : Fragment() {
    val itemsList: MutableList<Item> = mutableListOf()
    var itemAdapter: ItemsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)

    }

    override fun onViewCreated(pricesView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(pricesView, savedInstanceState)

        val context = context as DashboardActivity

        val listView: ListView = context.itemsListView

        itemAdapter = ItemsAdapter(context, R.layout.item_row, itemsList)

        listView.adapter = itemAdapter

        loadItems()

    }


    companion object {

        fun newInstance(): ItemsFragment = ItemsFragment()
    }


    private fun loadItems() {

        Fuel.get("https://5bbf0b90a510.ngrok.io/fetchAllItems")
            .response { _, _, result ->
                val (payload, error) = result

                val items: Map<String, Any?> = mapper.readValue(payload!!)
                val data = items["data"] as Map<String, Map<String, String>>

                data.forEach {
                    itemsList.add(
                        mapper.convertValue(
                            it.value,
                            object : TypeReference<Item>() {})
                    )
                }
                itemAdapter!!.notifyDataSetChanged()
                itemProgressBar.visibility = View.GONE
            }


    }
}

