package com.secondhand.shop.screens.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.secondhand.shop.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        observeUser()
    }

    private fun observeUser() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    _user.value = snapshot.toObject(User::class.java)
                }
            }
    }

    // --- Optional manual refresh (rarely needed now) ---
    fun refreshUserOnce() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                _user.value = doc.toObject(User::class.java)
            }
    }

    // --- Clear user on logout ---
    fun clearUser() {
        _user.value = null
    }
}
