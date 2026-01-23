package com.secondhand.shop.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.*

@IgnoreExtraProperties
data class Chat(
    @DocumentId
    val id: String = "",
    val members: List<String> = emptyList(),
    // These fields must match your Firestore field names exactly
    val lastMessage: String? = null,
    val lastMessageTime: Timestamp? = null,
    val memberNames: Map<String, String> = emptyMap(),
    val memberImages: Map<String, String> = emptyMap(),
    val unreadCounts: Map<String, Int> = emptyMap()
) {
    /**
     * Gets the name of the person the current user is talking to.
     */
    fun getOtherUserName(currentUserId: String?): String {
        if (currentUserId == null) return "User"
        return memberNames.filterKeys { it != currentUserId }.values.firstOrNull() ?: "User"
    }

    /**
     * Gets the profile image of the other user.
     */
    fun getOtherUserProfileImage(currentUserId: String?): String? {
        if (currentUserId == null) return null
        return memberImages.filterKeys { it != currentUserId }.values.firstOrNull()
    }

    /**
     * Formats the timestamp:
     * - Shows time (14:30) if it happened today.
     * - Shows date (Jan 23) if it happened before today.
     */
    fun lastMessageTimeFormatted(): String {
        val date = lastMessageTime?.toDate() ?: return ""
        val now = Calendar.getInstance()
        val chatTime = Calendar.getInstance().apply { time = date }

        val isToday = now.get(Calendar.YEAR) == chatTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == chatTime.get(Calendar.DAY_OF_YEAR)

        val pattern = if (isToday) "HH:mm" else "MMM dd"
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }

    /**
     * Returns the unread count specifically for the current user.
     */
    fun unreadCountForUser(currentUserId: String?): Int {
        if (currentUserId == null) return 0
        return unreadCounts[currentUserId] ?: 0
    }
}