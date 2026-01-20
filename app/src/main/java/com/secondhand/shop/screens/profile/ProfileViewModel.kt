package com.secondhand.shop.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.secondhand.shop.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    init {
        refreshUser()
    }

    fun refreshUser() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val userData = doc.toObject(User::class.java)
                    _user.value = userData
                }
        }
    }

    // --- Clear user (for logout) ---
    fun clearUser() {
        _user.value = null
    }
}
