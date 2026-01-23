package com.secondhand.shop.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    @DocumentId val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "system", // Values: "like", "order", "system"
    val timestamp: Timestamp? = null,
    val userId: String = ""
) {
    /**
     * ✅ This function converts the Firebase Timestamp into a String.
     * If the timestamp is null, it returns an empty string.
     */
    fun formattedTime(): String {
        val date = timestamp?.toDate() ?: return ""
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}