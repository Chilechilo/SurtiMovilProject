package com.surtiapp.surtimovil.core.orders.viewmodel

import androidx.lifecycle.ViewModel
import com.surtiapp.surtimovil.addcart.model.Producto
import com.surtiapp.surtimovil.core.orders.model.Order
import com.surtiapp.surtimovil.core.orders.model.OrderProduct
import com.surtiapp.surtimovil.core.orders.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class OrdersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Iniciar con lista vacía - los pedidos se crearán desde el carrito
        _uiState.value = OrdersUiState(orders = emptyList())
    }
    fun createOrderFromCart(productosCarrito: List<Producto>) {
        if (productosCarrito.isEmpty()) return

        // Convertir productos del carrito a OrderProduct
        val orderProducts = productosCarrito.map { producto ->
            OrderProduct(
                id = producto.id,
                name = producto.nombre,
                quantity = producto.cantidadEnCarrito,
                unitPrice = producto.precio
            )
        }

        // Calcular el total
        val total = productosCarrito.sumOf { it.precio * it.cantidadEnCarrito }

        // Crear la nueva orden
        val newOrder = Order(
            id = "order_${UUID.randomUUID().toString().take(8)}",
            date = System.currentTimeMillis(),
            total = total,
            status = OrderStatus.PENDING,
            products = orderProducts
        )

        // Agregar la orden a la lista existente
        val currentOrders = _uiState.value.orders.toMutableList()
        currentOrders.add(0, newOrder) // Agregar al inicio de la lista

        // Actualizar el estado
        _uiState.value = _uiState.value.copy(orders = currentOrders)
    }

    /**
     * Obtiene productos recomendados basados en la frecuencia de compra
     * Retorna los productos más comprados por el usuario
     */
    fun getRecommendedProducts(): List<RecommendedProduct> {
        val currentOrders = _uiState.value.orders

        // Contar frecuencia de cada producto
        val productFrequency = mutableMapOf<String, ProductFrequency>()

        currentOrders.forEach { order ->
            order.products.forEach { orderProduct ->
                val current = productFrequency[orderProduct.id]
                if (current != null) {
                    productFrequency[orderProduct.id] = current.copy(
                        count = current.count + orderProduct.quantity,
                        totalSpent = current.totalSpent + orderProduct.subtotal
                    )
                } else {
                    productFrequency[orderProduct.id] = ProductFrequency(
                        id = orderProduct.id,
                        name = orderProduct.name,
                        count = orderProduct.quantity,
                        unitPrice = orderProduct.unitPrice,
                        totalSpent = orderProduct.subtotal
                    )
                }
            }
        }

        // Convertir a lista y ordenar por frecuencia (más comprados primero)
        return productFrequency.values
            .sortedByDescending { it.count }
            .map { freq ->
                RecommendedProduct(
                    id = freq.id,
                    name = freq.name,
                    unitPrice = freq.unitPrice,
                    timesPurchased = freq.count,
                    totalSpent = freq.totalSpent,
                    imageUrl = "" // Se llenará desde el catálogo en la UI
                )
            }
    }
}

/**
 * Datos internos para contar frecuencia de productos
 */
private data class ProductFrequency(
    val id: String,
    val name: String,
    val count: Int,
    val unitPrice: Double,
    val totalSpent: Double
)

/**
 * Producto recomendado con estadísticas de compra
 */
data class RecommendedProduct(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val timesPurchased: Int,
    val totalSpent: Double,
    val imageUrl: String
)