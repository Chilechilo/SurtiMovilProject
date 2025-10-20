package com.surtiapp.surtimovil.Addcarrito.model

// Usamos data class para que Kotlin genere automáticamente métodos como equals() y copy()
data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imageUrl: String,
    val cantidadEnCarrito: Int = 0
)
