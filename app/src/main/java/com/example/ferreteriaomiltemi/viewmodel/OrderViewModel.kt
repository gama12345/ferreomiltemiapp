package com.example.ferreteriaomiltemi.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ferreteriaomiltemi.domain.model.Order
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.domain.repository.OrderRepository
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.DocumentReference
import com.google.gson.Gson
import java.lang.reflect.Type

class OrderViewModel(application: Application) : ViewModel(){
    private val repository = OrderRepository(application)
    var orders = MutableLiveData<ArrayList<Order>>()

    suspend fun register(newOrder: Order): DocumentReference?{
        return repository.registerNewOrder(newOrder)
    }

    suspend fun getOrdersByClient(clientId: String) {
        var docs = repository.getOrdersByClient(clientId)
        var docsArray: ArrayList<Order> = arrayListOf()
        for(doc in docs){
            val products = doc.get("products") as ArrayList<HashMap<Int, Product>>
            var list = arrayListOf<Product>()
            for(product in products){
                list.add(Product(
                        product.get("id").toString(),
                        product.get("name").toString(),
                        product.get("short_name").toString(),
                        product.get("brand").toString(),
                        product.get("category").toString(),
                        product.get("material").toString(),
                        product.get("price").toString(),
                        product.get("cost").toString(),
                        product.get("discount").toString(),
                        product.get("sales_unit").toString(),
                        product.get("description").toString(),
                        product.get("stock").toString(),
                        product.get("supplier").toString(),
                        product.get("image").toString(),
                        product.get("quantity").toString()
                ))
            }
            docsArray.add(
                    Order(doc.id,
                            doc.getString("client").toString(),
                            list, doc.getString("date").toString(),
                            doc.getString("status").toString())
            )

        }
        orders.postValue(docsArray)
    }

    suspend fun cancelOrder(id: String, client: String){
        repository.cancelOrder(id)
    }
}