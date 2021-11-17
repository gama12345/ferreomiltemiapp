package com.example.ferreteriaomiltemi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ferreteriaomiltemi.domain.model.Product
import com.example.ferreteriaomiltemi.domain.repository.ProductRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProductViewModel(): ViewModel() {
    private val repository = ProductRepository()
    var products = MutableLiveData<ArrayList<Product>>()

    suspend fun getProductsBy(sortBy: String, value: String){
        val docs = repository.getProductsBy(sortBy, value)
        setArrayToProductsList(docs)
    }

    suspend fun getProductsWithDiscount(){
        val docs = repository.getProductsWithDiscount()
        setArrayToProductsList(docs)
    }

    private fun setArrayToProductsList(docs: MutableList<DocumentSnapshot>){
        var docsArray: ArrayList<Product> = arrayListOf()
        GlobalScope.launch(Dispatchers.Main) {
            for (doc in docs) {
                var discountName = doc.get("discount").toString()
                if (discountName != "Indefinido") {
                    val res = repository.getDiscountById(discountName)
                    discountName = res?.getString("description").toString()
                }
                val imagesField = doc.get("images").toString()
                var imageURL = imagesField.substringAfter("url=").substringBefore("}}").substringBefore("},")
                docsArray.add(
                        Product(doc.id,
                                doc.get("name").toString(),
                                doc.get("short_name").toString(),
                                doc.get("brand").toString(),
                                doc.get("category").toString(),
                                doc.get("material").toString(),
                                doc.get("price").toString(),
                                doc.get("cost").toString(),
                                discountName,
                                doc.get("sales_unit").toString(),
                                doc.get("description").toString(),
                                doc.get("stock").toString(),
                                doc.get("supplier").toString(),
                                imageURL
                        )
                )
            }
            products.postValue(docsArray)
        }
    }
}
