package com.example.ferreteriaomiltemi.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ClientViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ClientViewModel::class.java)){
            return ClientViewModel(application) as T
        }
        throw IllegalArgumentException("Error al pasar argumentos a AuthViewModel")
    }
}