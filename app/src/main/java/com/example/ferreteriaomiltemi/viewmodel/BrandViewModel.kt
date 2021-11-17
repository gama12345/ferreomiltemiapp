package com.example.ferreteriaomiltemi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ferreteriaomiltemi.domain.model.Category
import com.example.ferreteriaomiltemi.domain.model.Client
import com.example.ferreteriaomiltemi.domain.repository.BrandRepository
import com.example.ferreteriaomiltemi.domain.repository.CategoryRepository
import com.example.ferreteriaomiltemi.domain.repository.ClientRepository

class BrandViewModel(): ViewModel() {
    private val repository = BrandRepository()
    var brands = MutableLiveData<ArrayList<String>>()

    suspend fun getAllBrands(){
        val docs = repository.getAll()
        var docsArray: ArrayList<String> = arrayListOf()
        for(doc in docs){
            docsArray.add(doc.get("name").toString())
        }
        brands.postValue(docsArray)
    }
}