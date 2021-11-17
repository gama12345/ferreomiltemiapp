package com.example.ferreteriaomiltemi.domain.repository

import android.app.Application
import android.util.Base64.encodeToString
import android.widget.Toast
import com.example.ferreteriaomiltemi.domain.model.Client
import com.example.ferreteriaomiltemi.domain.model.NewClient
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Base64
import kotlinx.coroutines.tasks.await

class ClientRepository (application: Application){
    private val application = application
    private val COLLECTION_NAME = "clients"
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerNewClient(newClient: NewClient): DocumentReference? {
        var res: DocumentReference? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val encodedPass = Base64.getEncoder().encodeToString(newClient.password.toByteArray())
            val newClient = hashMapOf(
                    "name" to newClient.name,
                    "last_name" to newClient.last_name,
                    "email" to newClient.email,
                    "password" to encodedPass,
                    "phone" to newClient.phone
            )
            try {
                res = db.collection(COLLECTION_NAME)
                        .add(newClient)
                        .await()
            }catch(e: Exception){
                Toast.makeText(application, e.toString(), Toast.LENGTH_SHORT).show()
            }
            return res
        } else {
            val toastMsg = "Su dispositivo no cuenta con la versón minima para ejecutar la app"
            Toast.makeText(application, toastMsg, Toast.LENGTH_SHORT).show()
            return res
        }
    }

    suspend fun updateClient(client: Client){
        val updatedData = hashMapOf<String, Any>(
            "name" to client.name,
            "last_name" to client.last_name,
            "phone" to client.phone,
            "email" to client.email,
            "password" to client.password
        )
        db.collection(COLLECTION_NAME)
            .document(client.id)
            .update(updatedData)
            .addOnCompleteListener { task ->
                var toastMsg = ""
                when(task.isSuccessful){
                    true -> toastMsg = "Se han guardado los cambios"
                    false -> toastMsg = "Ha ocurro un error: "+task.exception.toString()
                }
                Toast.makeText(application, toastMsg, Toast.LENGTH_SHORT).show()
            }
    }

    suspend fun isClientRegistered(email: String): Boolean {
        val res = db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .get().await()
        return res.isEmpty
    }

    suspend fun getClientByEmail(email: String): Client? {
        val res = db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1)
                .get().await()
        val data = res.documents[0].data
        if(data != null){
            val name = data["name"].toString()
            val lastName = data["last_name"].toString()
            val phone = data["phone"].toString()
            val email = data["email"].toString()
            val password = data["password"].toString()
            val client = Client(res.documents[0].id, name, lastName, phone, email, password)
            return client
        }
        return null
    }
}