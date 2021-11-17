package com.example.ferreteriaomiltemi.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArraySet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.ActivityMainBinding
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.domain.preferences.SharedApp
import com.example.ferreteriaomiltemi.view.auth.AuthActivity
import com.example.ferreteriaomiltemi.view.client.ClientAccountActivity
import com.example.ferreteriaomiltemi.view.ordercart.OrderCartActivity
import com.example.ferreteriaomiltemi.view.products.ProductsActivity
import com.example.ferreteriaomiltemi.viewmodel.*
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authViewModelFactory: AuthViewModelFactory
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var orderViewModelFactory: OrderViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.startToolbar.mainToolbar)
        supportActionBar?.title = "Inicio"

        val bottomNavController = supportFragmentManager.findFragmentById(R.id.fragmentBottomNavView) as NavHostFragment
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.fragment_main_start, R.id.fragment_main_orders))
        setupActionBarWithNavController(bottomNavController.navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(bottomNavController.navController)
        // View Models
        orderViewModelFactory = OrderViewModelFactory(application)
        orderViewModel = ViewModelProvider(this, orderViewModelFactory).get(OrderViewModel::class.java)
        authViewModelFactory = AuthViewModelFactory(application)
        authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
        authViewModel.userData.observe(this, Observer { user ->
            if(user == null){
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.optionmenu_logout -> authViewModel.logout()
            R.id.optionmenu_profile -> {
                intent = Intent(this, ClientAccountActivity::class.java)
                intent.putExtra("previous", "MainActivity")
                startActivity(intent)
            }
            R.id.orderBasket -> {
                intent = Intent(this, OrderCartActivity::class.java)
                intent.putExtra("previous", "MainActivity")
                startActivity(intent)
            }
        }
        return true
    }
}