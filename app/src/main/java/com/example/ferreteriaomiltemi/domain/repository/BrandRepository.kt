package com.example.ferreteriaomiltemi.domain.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BrandRepository (){
    private val COLLECTION_NAME = "brands"
    private val db = FirebaseFirestore.getInstance()

    suspend fun getAll(): MutableList<DocumentSnapshot> {
        val res = db.collection(COLLECTION_NAME)
            .get()
            .await()
        return res.documents
    }
}