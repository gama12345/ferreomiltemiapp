package com.example.ferreteriaomiltemi.domain.model

data class Product(
        val id: String,
        val name: String,
        val short_name: String,
        val brand: String,
        val category: String,
        val material: String,
        val price: String,
        val cost: String,
        val discount: String,
        val sales_unit: String,
        val description: String,
        val stock: String,
        val supplier: String,
        var image: String,
        var quantity: String = "1"
)
