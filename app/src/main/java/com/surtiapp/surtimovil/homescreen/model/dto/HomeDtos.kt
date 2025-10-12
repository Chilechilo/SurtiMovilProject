package com.surtiapp.surtimovil.homescreen.model.dto

data class HomeResponse(
    val home: List<Category>
)

data class Category(
    val categoria: String,
    val productos: List<Product>
)

data class Product(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val imagen: String
)