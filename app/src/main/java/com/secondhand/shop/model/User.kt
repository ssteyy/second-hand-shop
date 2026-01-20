package com.secondhand.shop.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val bio: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
