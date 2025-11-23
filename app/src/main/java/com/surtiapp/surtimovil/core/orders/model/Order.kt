package com.surtiapp.surtimovil.core.orders.model

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Estado del pedido
 */
enum class OrderStatus {
    DELIVERED,    // Entregado
    PENDING,      // Pendiente
    CANCELLED     // Cancelado
}

/**
 * Producto dentro de un pedido
 */
data class OrderProduct(
    val id: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val subtotal: Double
        get() = quantity * unitPrice
}

/**
 * Pedido completo
 */
data class Order(
    val id: String,
    val date: Long, // timestamp en milisegundos
    val total: Double,
    val status: OrderStatus,
    val products: List<OrderProduct>
) {
    /**
     * Formatea la fecha del pedido
     */
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(date))
    }

    /**
     * Formatea el total con símbolo de moneda
     */
    fun getFormattedTotal(): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        return format.format(total)
    }
}