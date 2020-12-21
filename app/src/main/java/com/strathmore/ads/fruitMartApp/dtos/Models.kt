package com.strathmore.ads.fruitMartApp.dtos

import java.io.Serializable
import java.time.Instant

data class Item(
    val name: String? = null,
    val price: Int? = null,
    val unit: String? = null,
    val lastModified: String? = null
) : Serializable

data class Orders(
    val cashierName: String? = "",
    val orderDate: String? = Instant.now().toString(),
    val orderItems: List<OrderItem>? = null,
    val orderTotal: Double? = 0.0,
    val amountPaid: Double? = 0.0
) : Serializable

data class OrderItem(
    val item: Item? = null,
    var quantity: Double? = 0.0,
    var total: Double? = 0.0
) : Serializable