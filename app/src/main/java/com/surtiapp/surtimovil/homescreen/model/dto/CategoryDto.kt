package com.surtiapp.surtimovil.homescreen.model.dto

data class CategoryDto(
    val id: Int,
    val category: String,
    val products: List<ProductDto> = emptyList()
)
