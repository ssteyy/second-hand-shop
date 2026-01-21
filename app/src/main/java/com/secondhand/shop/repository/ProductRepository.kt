package com.secondhand.shop.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.secondhand.shop.model.Product

object ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private const val COLLECTION = "products"

    // ---------------------------
    // SAVE PRODUCT
    // ---------------------------
    fun saveProduct(
        product: Product,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid
            ?: return onError(Exception("User not logged in"))

        val docRef = db.collection(COLLECTION).document()

        val productWithRequiredFields = product.copy(
            id = docRef.id,
            sellerId = userId,   // ✅ Force current user as seller
            sold = false         // ✅ Use 'sold' to match Firestore
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
        soldStatus: Boolean, // ✅ renamed parameter for clarity
        onSuccess: (List<Product>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(COLLECTION)
            .whereEqualTo("sellerId", currentUserId)
            .whereEqualTo("sold", soldStatus) // ✅ Use 'sold' instead of 'isSold'
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
}
