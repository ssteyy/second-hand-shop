package com.secondhand.shop.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.secondhand.shop.model.Product

object ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private const val COLLECTION = "products"

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    // ---------------------------
    // SAVE PRODUCT
    // ---------------------------
    fun saveProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userId = currentUserId ?: return onError(Exception("User not logged in"))

        val docRef = db.collection(COLLECTION).document()

        val productWithRequiredFields = product.copy(
            id = docRef.id,
            sellerId = userId,
            sold = false
        )

        docRef.set(productWithRequiredFields)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // ---------------------------
    // GET PRODUCT BY ID
    // ---------------------------
    fun getProductById(
        productId: String,
        onSuccess: (Product?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(COLLECTION)
            .document(productId)
            .get()
            .addOnSuccessListener { snapshot ->
                val product = snapshot.toObject(Product::class.java)?.copy(id = snapshot.id)
                onSuccess(product)
            }
            .addOnFailureListener { onError(it) }
    }

    // ---------------------------
    // UPDATE PRODUCT
    // ---------------------------
    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (product.id.isBlank()) {
            onError(Exception("Product ID is empty"))
            return
        }

        db.collection(COLLECTION)
            .document(product.id)
            .set(product)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // ---------------------------
    // FETCH USER PRODUCTS
    // ---------------------------
    fun fetchUserProducts(
        currentUserId: String,
        soldStatus: Boolean,
        onSuccess: (List<Product>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(COLLECTION)
            .whereEqualTo("sellerId", currentUserId)
            .whereEqualTo("sold", soldStatus)
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                onSuccess(products)
            }
            .addOnFailureListener { onError(it) }
    }

    // ---------------------------
    // DELETE PRODUCT
    // ---------------------------
    fun deleteProduct(
        productId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(COLLECTION)
            .document(productId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // ---------------------------
// GET USER NAME BY ID
// ---------------------------
    fun getUserNameById(userId: String, onResult: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                // Ensure "username" matches the field name in your Firestore 'users' collection
                val name = document.getString("username") ?: document.getString("name")
                onResult(name)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // ---------------------------
    // FETCH ALL AVAILABLE PRODUCTS
    // ---------------------------
    fun fetchAllAvailableProducts(
        onSuccess: (List<Product>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(COLLECTION)
            .whereEqualTo("sold", false)
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull {
                    it.toObject(Product::class.java)?.copy(id = it.id)
                }
                onSuccess(products)
            }
            .addOnFailureListener { onError(it) }
    }

    // ---------------------------
    // TOGGLE FAVORITE
    // ---------------------------
    fun toggleFavorite(
        product: Product,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return onError(Exception("User not logged in"))

        val isFav = product.favorites.contains(currentUserId)
        val updatedFavorites = if (isFav) {
            product.favorites - currentUserId
        } else {
            product.favorites + currentUserId
        }

        FirebaseFirestore.getInstance()
            .collection("products")
            .document(product.id)
            .update("favorites", updatedFavorites)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }


    // ---------------------------
    // CHECK IF FAVORITE
    // ---------------------------
    fun isFavorite(product: Product, callback: (Boolean) -> Unit) {
        val userId = currentUserId
        if (userId == null) {
            callback(false)
        } else {
            callback(product.favorites.contains(userId))
        }
    }
}
