package com.example.ferreteriaomiltemi.domain.model

data class Order(
        val id: String,
        val client: String,
        val products: ArrayList<Product>,
        val date: String,
        val status: String
)