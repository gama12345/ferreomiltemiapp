package com.example.ferreteriaomiltemi.domain.repository

import android.app.Application
import android.widget.Toast
import com.example.ferreteriaomiltemi.domain.model.Order
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderRepository(application: Application) {
    private val application = application
    private val COLLECTION_NAME = "orders"
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerNewOrder(order: Order): DocumentReference? {
        var res : DocumentReference? = null
        val newOrder = hashMapOf(
                "client" to order.client,
                "products" to order.products,
                "date" to order.date,
                "status" to order.status
        )
        try {
            res = db.collection(COLLECTION_NAME)
                    .add(newOrder)
                    .await()
        }catch(e: Exception){
            Toast.makeText(application, e.toString(), Toast.LENGTH_SHORT).show()
        }
        return res
    }

    suspend fun getOrdersByClient(clientId: String): MutableList<DocumentSnapshot> {
        var res = db.collection(COLLECTION_NAME)
                .whereEqualTo("client", clientId)
                .get()
                .await()
        return res.documents
    }

    suspend fun cancelOrder(id: String){
        db.collection(COLLECTION_NAME)
            .document(id)
            .update("status", "CANCELED")
            .await()
    }
}