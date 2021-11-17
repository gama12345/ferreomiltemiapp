package com.example.ferreteriaomiltemi.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.ferreteriaomiltemi.domain.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application): ViewModel() {
    private val repository = AuthenticationRepository(application)
    val userData = repository.firebaseUser

    suspend fun register(email: String, password: String): FirebaseUser?{
        return repository.register(email, password)
    }

    suspend fun login(email: String, password: String){
        repository.login(email, password)
    }

    fun logout(){
        repository.logout()
    }

    suspend fun updateEmail(newEmail: String): Boolean{
        return repository.updateEmail(newEmail)
    }

    suspend fun updatePassword(newPassword: String): Boolean{
        return repository.updatePassword(newPassword)
    }
}