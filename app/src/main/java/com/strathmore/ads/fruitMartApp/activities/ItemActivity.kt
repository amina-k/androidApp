package com.strathmore.ads.fruitMartApp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.dtos.Item
import kotlinx.android.synthetic.main.activity_item.*

val mapper = ObjectMapper()
var itemsList: MutableList<String> = mutableListOf()


class ItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        fetchAllItems()
        save_new_item.setOnClickListener {
            val itemName = new_item.text.toString()
            val price = new_item_price.text.toString()
            val itemUnit = new_item_unit.text.toString()

            //Data Validation
            if (itemName.trim().isEmpty()) {
                Toast.makeText(baseContext, "Please enter a product name.", Toast.LENGTH_SHORT).show()
            } else if (price.trim().isEmpty()) {
                Toast.makeText(baseContext, "Please enter a price.", Toast.LENGTH_SHORT).show()
            } else if (itemUnit.trim().isEmpty()) {
                Toast.makeText(baseContext, "Please enter a price.", Toast.LENGTH_SHORT).show()
            } else {
                var numeric = true
                try {
                    price.toInt()
                } catch (e: NumberFormatException) {
                    numeric = false
                }

                if (numeric) {
                    if (itemName in itemsList) {
                        Toast.makeText(
                            baseContext,
                            "$itemName already exists, use update feature",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        addProductDialog(
                            Item(itemName, price.toInt(), itemUnit)
                        )
                    }

                } else {
                    Toast.makeText(baseContext, "Please enter a valid price e.g 20", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addProductDialog(item: Item) {

        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Sure you are ready to save?")
            .setCancelable(false)
            .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                addItem(item)
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("FruitMart")
        alert.show()

    }

    private fun fetchAllItems() {

        Fuel.get("https://09c5d121a16f.ngrok.io/fetchAllItems")
            .response { request, response, result ->
                val (payload, error) = result
                val items: Map<String, Any?> = mapper.readValue(payload!!)
                val data = items["data"] as Map<String, Map<String, String>>

                data.forEach {
                    itemsList.add(it.value.getValue("name"))
                }
            }
    }


    private fun addItem(item: Item) {
        val body = """ {"name" : "${item.name}", "price" : "${item.price}","unit" : "${item.unit}"}"""
        Fuel.post("https://09c5d121a16f.ngrok.io/addItem")
            .jsonBody(body)
            .response { request, response, result ->
                val (payload, error) = result
                val status: Map<String, String?> = mapper.readValue(payload!!)
                if (status["message"] == "success") {
                    Toast.makeText(baseContext, "Item successfully uploaded", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))

                } else {
                    Toast.makeText(baseContext, "Sorry, something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }

}


