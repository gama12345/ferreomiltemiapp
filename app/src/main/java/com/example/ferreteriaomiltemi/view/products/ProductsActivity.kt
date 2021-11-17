package com.example.ferreteriaomiltemi.view.products

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.ActivityProductsBinding
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.view.adapters.RecyclerAdapterProducts
import com.example.ferreteriaomiltemi.view.client.ClientAccountActivity
import com.example.ferreteriaomiltemi.view.main.MainActivity
import com.example.ferreteriaomiltemi.view.ordercart.OrderCartActivity
import com.example.ferreteriaomiltemi.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authViewModelFactory: AuthViewModelFactory
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var brandViewModel: BrandViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var filterOption: String
    val adapterRV: RecyclerAdapterProducts = RecyclerAdapterProducts()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Toolbar config
        setSupportActionBar(binding.productsToolbar.mainToolbar)
        supportActionBar?.title = "Productos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // ViewModels
        authViewModelFactory = AuthViewModelFactory(application)
        authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)
        productViewModel = ProductViewModel()
        categoryViewModel = CategoryViewModel()
        brandViewModel = BrandViewModel()
        // Observers
        productViewModel.products.observe(this, Observer { products ->
            setUpRecyclerView(products)
            binding.tvTotalProductos.text = "${products.size} resultados"
            binding.progressBarProducts.visibility = View.GONE
            binding.svProducts.visibility = View.VISIBLE
        })
        categoryViewModel.categories.observe(this, Observer { categories -> setFilterList(categories) })
        brandViewModel.brands.observe(this, Observer { brands -> setFilterList(brands) })
        // Filter
        filterOption = intent.extras?.get("filter").toString()
        when(filterOption){
            "category" -> GlobalScope.launch(Dispatchers.Main) { categoryViewModel.getAllCategories() }
            "brand" -> GlobalScope.launch(Dispatchers.Main) { brandViewModel.getAllBrands() }
            "discounts" -> GlobalScope.launch(Dispatchers.Main) {
                binding.filterProductsSpinner.visibility = View.GONE
                productViewModel.getProductsWithDiscount()
            }
        }
        // Progress bar
        binding.progressBarProducts.visibility = View.VISIBLE
        binding.svProducts.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            R.id.optionmenu_logout -> authViewModel.logout()
            R.id.optionmenu_profile -> {
                intent = Intent(this, ClientAccountActivity::class.java)
                intent.putExtra("previous", "ProductsActivity")
                startActivity(intent)
            }
            R.id.orderBasket -> {
                intent = Intent(this, OrderCartActivity::class.java)
                intent.putExtra("filter", filterOption)
                intent.putExtra("previous", "ProductsActivity")
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun setUpRecyclerView(list: ArrayList<Product>){
        binding.rvProducts.setHasFixedSize(true)
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        adapterRV.RecyclerAdapterProducts(list, this)
        binding.rvProducts.adapter = adapterRV
    }

    fun setFilterList(list: ArrayList<String>){
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        binding.filterProductsSpinner.adapter = adapter
        binding.filterProductsSpinner.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                binding.progressBarProducts.visibility = View.VISIBLE
                binding.svProducts.visibility = View.GONE
                GlobalScope.launch {
                    productViewModel.getProductsBy(filterOption, binding.filterProductsSpinner.selectedItem.toString())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

}