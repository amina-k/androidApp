package com.strathmore.ads.fruitMartApp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.adapters.AddOrderAdapter
import com.strathmore.ads.fruitMartApp.dtos.Item
import com.strathmore.ads.fruitMartApp.dtos.OrderItem
import com.strathmore.ads.fruitMartApp.dtos.Orders
import kotlinx.android.synthetic.main.activity_order.*

class OrderActivity : AppCompatActivity() {

    var addOrderAdapter: AddOrderAdapter? = null
    val itemsNameList: MutableList<String> = mutableListOf()
    val itemsList: MutableList<Item> = mutableListOf()
    val temporaryItemsList: MutableList<OrderItem> = mutableListOf()
    private lateinit var selectedItem: Item
    private var orderTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val listView: ListView = orderItemsListView

        addOrderAdapter = AddOrderAdapter(this, R.layout.add_order_item_row, temporaryItemsList)

        listView.adapter = addOrderAdapter

        val itemSpinner = itemSpinner
        itemSpinner.setEnabled(true)

        val orderItemAdapter: ArrayAdapter<String> =
            ArrayAdapter(baseContext, R.layout.custom_spinner_item, itemsNameList)


        Fuel.get("https://09c5d121a16f.ngrok.io/fetchAllItems")
            .response { _, _, result ->
                val (payload, error) = result
                val newItems: Map<String, Any?> = mapper.readValue(payload!!)
                val data = newItems["data"] as Map<String, Map<String, String>>
                itemSpinner.adapter = orderItemAdapter

                data.forEach {

                    itemsNameList.add(
                        mapper.convertValue(
                            it.value["name"],
                            object : TypeReference<String>() {})
                    )

                    itemsList.add(
                        mapper.convertValue(
                            it.value,
                            object : TypeReference<Item>() {})
                    )
                }

                itemSpinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        val selectedItemName = itemsNameList[position]
                        selectedItem = itemsList.filter{ it.name!!.equals(selectedItemName)}[0]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

                orderItemAdapter.notifyDataSetChanged()
            }


        val addNewItemView = add_new_item
        addNewItemView.setOnClickListener {

            val inputQuantity = itemQuanity.text.toString()


            //Data Validation
                val validQuantity = checkNumeric(inputQuantity)

                if (validQuantity) {
                    var addNew = true
                    for (orderItem in temporaryItemsList) {

                        if (orderItem.item!!.name == selectedItem.name) {
                            Toast.makeText(baseContext, "Updating quantity and total for: $selectedItem.", Toast.LENGTH_SHORT).show()

                            val newQuantity = inputQuantity.toDouble()
                            val newTotal = newQuantity * orderItem.item.price!!
                            orderTotal -= orderItem.total!!
                            orderItem.quantity = newQuantity
                            orderItem.total = newTotal
                            addOrderAdapter!!.notifyDataSetChanged()
                            orderTotal += orderItem.total!!
                            newOrderTotal.text = "Order Total: Ksh. ${orderTotal}"

                            addNew = false
                        }

                    }

                    if (addNew) {
                        addOrderItem()
                    }

                } else {
                    Toast.makeText(baseContext, "Please enter a valid quantity e.g 0.5", Toast.LENGTH_SHORT).show()
                }
            }

        val saveOrderView = save_order
        saveOrderView.setOnClickListener {
            val enteredName = newCashier.text.toString()
            val paymentAmount = amountPaid.text.toString()

            //Data Validation
            when {
                enteredName.trim().isEmpty() -> Toast.makeText(baseContext, "Please enter the cashier's name.", Toast.LENGTH_SHORT).show()
                paymentAmount.trim().isEmpty() -> {
                    Toast.makeText(baseContext, "Please enter the payment amount.", Toast.LENGTH_SHORT).show()
                    val validAmount = checkNumeric(paymentAmount)
                    if(!validAmount){
                        Toast.makeText(baseContext, "Please enter a valid payment amount e.g 2000", Toast.LENGTH_SHORT).show()
                    }
                }
                temporaryItemsList.isEmpty() -> Toast.makeText(baseContext, "Please select at least 1 item to order", Toast.LENGTH_SHORT).show()
                else -> addOrderDialog()
            }
        }
    }

    private fun addOrderItem() {
        val quantity = itemQuanity.text.toString().toDouble()
        val body = """ {"name" : "${selectedItem.name}", "quantity" : ${quantity} } """
        Fuel.post("https://09c5d121a16f.ngrok.io/calcItemCost")
            .jsonBody(body)
            .response { request, response, result ->
                val (payload, error) = result
                val status: Map<String, Any?> = mapper.readValue(payload!!)
                if (status["message"] == "success") {

                    val data = status["data"] as Map<String, String>
                    val total = data["totalCost"]!!.toDouble()

                    temporaryItemsList.add(OrderItem(
                        item = selectedItem,
                        quantity = quantity,
                        total = total))
                    orderTotal += total

                    newOrderTotal.text = "Order Total: Ksh. ${orderTotal}"

                    orderItemsListView.visibility = View.VISIBLE
                    addOrderAdapter!!.notifyDataSetChanged()


                } else {
                    Toast.makeText(baseContext, "Sorry, something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

        addOrderAdapter!!.notifyDataSetChanged()
        itemQuanity.text.clear()


    }

    private fun addOrderDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val enteredName = newCashier.text.toString()
        dialogBuilder.setMessage("Sure you are ready to save?")
            .setCancelable(false)
            .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                addOrderFirebase(temporaryItemsList, enteredName, orderTotal)
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("FruitMart")
        alert.show()

    }

    private fun checkNumeric(value:  String):Boolean {

        var numeric = true
        try {
            value.toDouble()
        } catch (e: NumberFormatException) {
            numeric = false
        }
        return numeric
    }

    private fun addOrderFirebase(orderItems: List<OrderItem>, cashierName: String, finalTotal: Double) {

        val order = Orders(
            cashierName = cashierName,
            orderTotal = finalTotal,
            orderItems = orderItems,
            amountPaid = amountPaid.text.toString().toDouble()
        )
        val body = mapper.convertValue(
            order,
            object : TypeReference<Map<String, Any>>() {}).toString()
        val json = Gson().toJson(order)
        println(json)

        Fuel.post("https://09c5d121a16f.ngrok.io/addOrder")
            .jsonBody(json)
            .response { request, response, result ->
                val (payload, error) = result
                val status: Map<String, String?> = mapper.readValue(payload!!)
                if (status["message"] == "success") {
                    Toast.makeText(
                        baseContext, "Successfully added $cashierName's order",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, SingleOrderActivity::class.java)
                    intent.putExtra("ORDER", order)
                    startActivity(intent)

                } else {
                    Toast.makeText(baseContext, "Sorry, something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }
}