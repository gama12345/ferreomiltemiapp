package com.example.ferreteriaomiltemi.view.ordercart

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.ActivityOrderCartBinding
import com.example.ferreteriaomiltemi.domain.model.Order
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.domain.preferences.SharedApp
import com.example.ferreteriaomiltemi.domain.repository.AuthenticationRepository
import com.example.ferreteriaomiltemi.view.adapters.RecyclerAdapterOrderCart
import com.example.ferreteriaomiltemi.view.adapters.RecyclerAdapterProducts
import com.example.ferreteriaomiltemi.view.client.ClientAccountActivity
import com.example.ferreteriaomiltemi.view.main.MainActivity
import com.example.ferreteriaomiltemi.view.products.ProductsActivity
import com.example.ferreteriaomiltemi.viewmodel.*
import com.google.android.material.snackbar.Snackbar
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderCartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderCartBinding
    private var extras: Bundle? = null
    private lateinit var filterOption: String
    private lateinit var productViewModel: ProductViewModel
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var orderViewModelFactory: OrderViewModelFactory
    val adapterRV: RecyclerAdapterOrderCart = RecyclerAdapterOrderCart()
    lateinit var repository: AuthenticationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Toolbar config
        setSupportActionBar(binding.orderCartToolbar.mainToolbar)
        supportActionBar?.title = "Canasta de productos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Repo
        repository = AuthenticationRepository(this.application)
        // Get previous activity
        extras = intent?.extras
        // Filter
        filterOption = extras?.get("filter").toString()
        // ViewModel
        orderViewModelFactory = OrderViewModelFactory(application)
        orderViewModel = ViewModelProvider(this, orderViewModelFactory).get(OrderViewModel::class.java)
        productViewModel = ProductViewModel()
        productViewModel.products.observe(this, Observer { products ->
            setUpRecyclerView(products)
            binding.progressBarOrderCart.visibility = View.GONE
            binding.svOrderCart.visibility = View.VISIBLE
            binding.tvOrdercartNoItems.visibility = View.GONE
            binding.btnOrdercartOrder.visibility = View.VISIBLE
            if(products.size == 0){
                binding.tvOrdercartNoItems.visibility = View.VISIBLE
                binding.btnOrdercartOrder.visibility = View.GONE
            }
        })
        // Events
        binding.btnOrdercartOrder.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Confirma tu orden")
            alertDialog.setMessage("¿Estas seguro de que deseas ordenar estos productos?")
            alertDialog.setPositiveButton("Ordenar"){ dialogInterface: DialogInterface, i: Int ->
                GlobalScope.launch(Dispatchers.Main) {
                    val clientId = repository.firebaseUser.value?.uid.toString()
                    val date = Calendar.getInstance().time
                    val formatter = SimpleDateFormat("dd/MM/yyyy")
                    val formatedDate = formatter.format(date)
                    val gson = Gson()
                    val type: Type = object : TypeToken<ArrayList<Product>>(){}.getType()
                    val orderCart: ArrayList<Product> = gson.fromJson(SharedApp.prefs.orderCart, type)
                    val newOrder = Order("", clientId, orderCart, formatedDate, "ACTIVE")
                    val res = orderViewModel.register(newOrder)
                    if(res != null) {
                        val editor = SharedApp.prefs.prefsOrderCart.edit()
                        orderCart.clear()
                        editor.putString("carrito", gson.toJson(orderCart))
                        editor.apply()
                        productViewModel.products.postValue(orderCart)
                        Toast.makeText(applicationContext, "Orden registrada", Toast.LENGTH_LONG).show()
                    }
                }
            }
            alertDialog.setNegativeButton("Regresar"){ dialogInterface: DialogInterface, i: Int -> }
            alertDialog.show()
        }
        // Progress bar
        binding.progressBarOrderCart.visibility = View.VISIBLE
        binding.svOrderCart.visibility = View.GONE
        getOrderCart()
    }

    fun getOrderCart(){
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<Product>>(){}.getType()
        val orderCart: ArrayList<Product> = gson.fromJson(SharedApp.prefs.orderCart, type)
        productViewModel.products.postValue(orderCart)
    }

    fun setUpRecyclerView(list: ArrayList<Product>){
        binding.rvOrderCart.setHasFixedSize(true)
        binding.rvOrderCart.layoutManager = LinearLayoutManager(this)
        adapterRV.RecyclerAdapterOrderCart(list, this, productViewModel)
        binding.rvOrderCart.adapter = adapterRV
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                var intent = Intent(this, MainActivity::class.java)
                if(extras?.get("previous") == "ProductsActivity"){
                    intent = Intent(this, ProductsActivity::class.java)
                    intent.putExtra("filter", filterOption)
                }
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}