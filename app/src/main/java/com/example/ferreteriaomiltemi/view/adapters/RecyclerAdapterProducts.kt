package com.example.ferreteriaomiltemi.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.domain.preferences.SharedApp
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.lang.reflect.Type

class RecyclerAdapterProducts : RecyclerView.Adapter<RecyclerAdapterProducts.ViewHolder>() {
    var products: MutableList<Product> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapterProducts(list: ArrayList<Product>, context: Context){
        products = list
        this.context = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById(R.id.imv_product_image) as ImageView
        val name = itemView.findViewById(R.id.tv_product_name) as TextView
        val price = itemView.findViewById(R.id.tv_product_price) as TextView
        val salesUnit = itemView.findViewById(R.id.tv_product_salesUnit) as TextView
        val discount = itemView.findViewById(R.id.tv_product_discount) as TextView
        val btnAddOrder = itemView.findViewById(R.id.btn_carrito_agregar) as Button

        fun bind(product: Product, context: Context){
            if(product.image != "null") {
                image.loadUrl(product.image)
            }
            name.text = product.short_name
            price.text = "$ ${product.price}"
            salesUnit.text = product.sales_unit
            if(product.discount == "Indefinido"){
                discount.visibility = View.GONE
            }else discount.text = product.discount
            btnAddOrder.setOnClickListener {
                val gson = Gson()
                val type: Type = object : TypeToken<ArrayList<Product>>(){}.getType()
                val orderCart: ArrayList<Product> = gson.fromJson(SharedApp.prefs.orderCart, type)
                if(!orderCart.contains(product)){
                    orderCart.add(product)
                    val editor = SharedApp.prefs.prefsOrderCart.edit()
                    editor.putString("carrito", gson.toJson(orderCart))
                    editor.apply()
                    Toast.makeText(context, "Producto agregado", Toast.LENGTH_SHORT).show()
                }else Toast.makeText(context, "Este producto ya se encuentra agregado", Toast.LENGTH_SHORT).show()
                Log.d("CARRITO >>> ", SharedApp.prefs.orderCart)
            }
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_product, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products.get(position)
        holder.bind(item, context)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}