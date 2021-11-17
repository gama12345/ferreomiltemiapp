package com.example.ferreteriaomiltemi.domain.repository

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class ProductRepository() {
    private val COLLECTION_NAME = "products"
    private val db = FirebaseFirestore.getInstance()

    suspend fun getProductsBy(sortBy: String, value: String): MutableList<DocumentSnapshot> {
        val res = db.collection(COLLECTION_NAME)
            .whereEqualTo(sortBy, value)
            .get()
            .await()
        return res.documents
    }

    suspend fun getProductsWithDiscount(): MutableList<DocumentSnapshot>{
        val res = db.collection(COLLECTION_NAME)
                .whereNotEqualTo("discount", "Indefinido")
                .get()
                .await()
        return res.documents
    }

    suspend fun getDiscountById(id: String): DocumentSnapshot? {
        val res = db.collection("discounts")
                .document(id)
                .get()
                .await()
        return res
    }

}