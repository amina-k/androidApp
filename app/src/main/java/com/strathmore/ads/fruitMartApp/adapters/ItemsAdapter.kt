package com.strathmore.ads.fruitMartApp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.activities.mapper
import com.strathmore.ads.fruitMartApp.dtos.Item
import kotlinx.android.synthetic.main.item_row.view.*


class ItemsAdapter(
    var itemContext: Context,
    var resource: Int,
    var items: MutableList<Item>
) : ArrayAdapter<Item>(itemContext, resource, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val itemView: View
        val layoutInflater: LayoutInflater = LayoutInflater.from(itemContext)

        itemView = layoutInflater.inflate(R.layout.item_row, parent, false)

        val item = items[position]

        val itemNameView = itemView.item
        val priceView = itemView.pricePerUnit


        itemNameView.text = item.name
        priceView.setText("Ksh: ${item.price} per ${item.unit}")

        val updateImg = itemView.updateItem

        updateImg.setOnClickListener {
            val newPrice = priceView.text.toString()
            updateItem(item.name!!, item.unit!!)
        }

        val deleteImg = itemView.deleteItem
        deleteImg.setOnClickListener {
            deleteItem(item.name!!)

        }
        return itemView
    }

    private fun updateItem(name:String, unit: String) {
        var newPrice: Int = 0

        val dialogBuilder = AlertDialog.Builder(context )
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        dialogBuilder.setView(input)

        dialogBuilder.setMessage("Please enter new price per $unit?")
            .setCancelable(false)
            .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                newPrice = input.text.toString().toInt()
                val body = """ {"name" : "$name", "price" : "$newPrice"}  """
                Fuel.post("https://09c5d121a16f.ngrok.io/updateItem")
                    .jsonBody(body)
                    .response { _, _, result ->
                        val (payload, error) = result
                        val status: Map<String, String?> = mapper.readValue(payload!!)

                        if (status["message"] == "success") {
                            Toast.makeText(context, "Item successfully updated", Toast.LENGTH_SHORT).show()
                            reloadItems()
                        } else {
                            Toast.makeText(context, "Sorry, something went wrong", Toast.LENGTH_SHORT).show()
                        }

                    }
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("FruitMart")
        alert.show()

    }

    private fun deleteItem(name: String) {
        val body = """ {"name" : "$name"}"""

        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setMessage("Sure you want to delete?")
            .setCancelable(false)
            .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                Fuel.post("https://09c5d121a16f.ngrok.io/deleteItem")
                    .jsonBody(body)
                    .response { _, _, result ->
                        val (payload, error) = result
                        val status: Map<String, String?> = mapper.readValue(payload!!)

                        if (status["message"] == "success") {
                            Toast.makeText(context, "Item successfully deleted", Toast.LENGTH_SHORT).show()
                            reloadItems()
                        } else {
                            Toast.makeText(context, "Sorry, something went wrong", Toast.LENGTH_SHORT).show()
                        }

                    }
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("FruitMart")
        alert.show()

    }

    private fun reloadItems(){
        Handler().postDelayed(
            {
                this.clear()

                Fuel.get("https://09c5d121a16f.ngrok.io/fetchAllItems")
                    .response { request, response, result ->
                        val (payload, error) = result
                        val newItems: Map<String, Any?> = mapper.readValue(payload!!)
                        val data = newItems["data"] as Map<String, Map<String, String>>

                        data.forEach {

                            items.add(
                                mapper.convertValue(
                                    it.value,
                                    object : TypeReference<Item>() {})
                            )
                        }

                        this.notifyDataSetChanged()
                    }
            },
            2000 // value in milliseconds
        )
    }


}