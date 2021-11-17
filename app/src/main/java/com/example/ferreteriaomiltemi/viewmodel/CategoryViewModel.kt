package com.example.ferreteriaomiltemi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ferreteriaomiltemi.domain.model.Category
import com.example.ferreteriaomiltemi.domain.model.Client
import com.example.ferreteriaomiltemi.domain.repository.CategoryRepository
import com.example.ferreteriaomiltemi.domain.repository.ClientRepository

class CategoryViewModel(): ViewModel() {
    private val repository = CategoryRepository()
    var categories = MutableLiveData<ArrayList<String>>()

    suspend fun getAllCategories(){
        val docs = repository.getAll()
        var docsArray: ArrayList<String> = arrayListOf()
        for(doc in docs){
            docsArray.add(doc.get("name").toString())
        }
        categories.postValue(docsArray)
    }
}