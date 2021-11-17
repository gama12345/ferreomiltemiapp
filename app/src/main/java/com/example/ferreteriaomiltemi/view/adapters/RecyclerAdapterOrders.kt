package com.example.ferreteriaomiltemi.view.adapters

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.domain.model.Order
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.domain.preferences.SharedApp
import com.example.ferreteriaomiltemi.viewmodel.OrderViewModel
import com.example.ferreteriaomiltemi.viewmodel.ProductViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class RecyclerAdapterOrders : RecyclerView.Adapter<RecyclerAdapterOrders.ViewHolder>() {
    var orders: MutableList<Order> = ArrayList()
    lateinit var context: Context
    lateinit var orderViewModel: OrderViewModel

    fun RecyclerAdapterOrders(list: ArrayList<Order>, context: Context, orderViewModel: OrderViewModel){
        orders = list
        this.context = context
        this.orderViewModel = orderViewModel
    }

    class ViewHolder(itemView: View, orderViewModel: OrderViewModel) : RecyclerView.ViewHolder(itemView){
        val date = itemView.findViewById(R.id.tv_order_date) as TextView
        val products = itemView.findViewById(R.id.tv_order_products) as TextView
        val status = itemView.findViewById(R.id.tv_order_status) as TextView
        val btnCancelOrder = itemView.findViewById(R.id.btn_order_cancel) as Button


        fun bind(order: Order, context: Context, orderViewModel: OrderViewModel){
            date.text = "Fecha: ${order.date}"
            when(order.status){
                "ACTIVE" -> status.text = "Estatus: Activa"
                "CANCELED" -> status.text = "Estatus: Cancelada"
                "CLOSED" -> status.text = "Estatus: Despachada"
            }

            var productsList = ""
            for(product in order.products){
                productsList += "Nombre: ${product.name} - Precio: ${product.price} \n"
            }
            products.text = productsList.substring(0, productsList.length-2)
            if(order.status == "CANCELED" || order.status == "CLOSED"){
                btnCancelOrder.visibility = View.GONE
            }else {
                btnCancelOrder.setOnClickListener {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle("Cancelar orden")
                    alertDialog.setMessage("¿Estas seguro de que deseas cancelar esta orden?")
                    alertDialog.setPositiveButton("Si") { dialogInterface: DialogInterface, i: Int ->
                        GlobalScope.launch(Dispatchers.Main) {
                            orderViewModel.cancelOrder(order.id, order.client)
                            status.text = "Estatus: Cancelada"
                            btnCancelOrder.visibility = View.GONE
                            Toast.makeText(context, "La orden ha sido cancelada", Toast.LENGTH_SHORT).show()
                        }
                    }
                    alertDialog.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int -> }
                    alertDialog.show()
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_order, parent, false), orderViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = orders.get(position)
        holder.bind(item, context, orderViewModel)
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}