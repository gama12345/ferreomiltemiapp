package com.example.ferreteriaomiltemi.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ferreteriaomiltemi.domain.model.Client
import com.example.ferreteriaomiltemi.domain.model.NewClient
import com.example.ferreteriaomiltemi.domain.repository.AuthenticationRepository
import com.example.ferreteriaomiltemi.domain.repository.ClientRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference

class ClientViewModel(application: Application): ViewModel() {
    private val repository = ClientRepository(application)
    var client = MutableLiveData<Client>()

    suspend fun register(newClient: NewClient): DocumentReference?{
        return repository.registerNewClient(newClient)
    }

    suspend fun setClientByEmail(email: String): Boolean{
        val res = repository.getClientByEmail(email) ?: return false
        client.postValue(res)
        return true
    }

    suspend fun updateClient(newData: Client){
        repository.updateClient(newData)
        client.postValue(newData)
    }
}