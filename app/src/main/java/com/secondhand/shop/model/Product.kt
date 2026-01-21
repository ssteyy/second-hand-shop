package com.secondhand.shop.model

data class Product(
    val id: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val condition: String = "",
    val imageUrl: String = "",
    val sellerId: String = "",
    val sold: Boolean = false,   // <--- match Firestore field
    val createdAt: Long = 0L
)

