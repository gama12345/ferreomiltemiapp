package com.example.ferreteriaomiltemi.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.domain.preferences.SharedApp
import com.example.ferreteriaomiltemi.viewmodel.ProductViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.lang.reflect.Type

class RecyclerAdapterOrderCart : RecyclerView.Adapter<RecyclerAdapterOrderCart.ViewHolder>() {
    var products: MutableList<Product> = ArrayList()
    lateinit var context: Context
    lateinit var productViewModel: ProductViewModel

    fun RecyclerAdapterOrderCart(list: ArrayList<Product>, context: Context, productViewModel: ProductViewModel){
        products = list
        this.context = context
        this.productViewModel = productViewModel
    }

    class ViewHolder(itemView: View, productViewModel: ProductViewModel) : RecyclerView.ViewHolder(itemView){
        val image = itemView.findViewById(R.id.imv_ordercart_image) as ImageView
        val name = itemView.findViewById(R.id.tv_ordercart_name) as TextView
        val price = itemView.findViewById(R.id.tv_ordercart_price) as TextView
        val salesUnit = itemView.findViewById(R.id.tv_ordercart_salesUnit) as TextView
        val discount = itemView.findViewById(R.id.tv_ordercart_discount) as TextView
        val btnRemoveOrder = itemView.findViewById(R.id.btn_ordercart_remover) as Button


        fun bind(product: Product, context: Context, productViewModel: ProductViewModel){
            if(product.image != "null") {
                image.loadUrl(product.image)
            }
            name.text = product.short_name
            price.text = "$ ${product.price}"
            salesUnit.text = product.sales_unit
            if(product.discount == "Indefinido"){
                discount.visibility = View.GONE
            }else discount.text = product.discount
            btnRemoveOrder.setOnClickListener {
                val gson = Gson()
                val type: Type = object : TypeToken<ArrayList<Product>>(){}.getType()
                val orderCart: ArrayList<Product> = gson.fromJson(SharedApp.prefs.orderCart, type)
                orderCart.remove(product)
                val editor = SharedApp.prefs.prefsOrderCart.edit()
                editor.putString("carrito", gson.toJson(orderCart))
                editor.apply()
                productViewModel.products.postValue(orderCart)
                Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_ordercart, parent, false), productViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products.get(position)
        holder.bind(item, context, productViewModel)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}