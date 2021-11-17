package com.example.ferreteriaomiltemi.view.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.ActivityClientAccountBinding
import com.example.ferreteriaomiltemi.domain.repository.AuthenticationRepository
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModel
import com.example.ferreteriaomiltemi.viewmodel.ClientViewModelFactory
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClientAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientAccountBinding
    private lateinit var clientViewModel: ClientViewModel
    private lateinit var clientViewModelFactory: ClientViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.clientToolbar.accountToolbar)

        // ViewModel instance
        clientViewModelFactory = ClientViewModelFactory(application)
        clientViewModel = ViewModelProvider(this, clientViewModelFactory).get(ClientViewModel::class.java)
    }
}