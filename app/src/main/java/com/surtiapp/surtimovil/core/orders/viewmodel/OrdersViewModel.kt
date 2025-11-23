package com.surtiapp.surtimovil.core.orders.viewmodel

import androidx.lifecycle.ViewModel
import com.surtiapp.surtimovil.core.orders.model.Order
import com.surtiapp.surtimovil.core.orders.model.OrderProduct
import com.surtiapp.surtimovil.core.orders.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class OrdersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMockOrders()
    }

    /**
     * Carga pedidos simulados (mock data)
     * En producción, esto vendría de un repositorio/API
     */
    private fun loadMockOrders() {
        val mockOrders = listOf(
            // Pedido 1: Entregado
            Order(
                id = "order_001",
                date = getDateDaysAgo(2),
                total = 1250.50,
                status = OrderStatus.DELIVERED,
                products = listOf(
                    OrderProduct("p1", "Coca-Cola 2L", 12, 25.00),
                    OrderProduct("p2", "Sabritas Original 45g", 24, 12.50),
                    OrderProduct("p3", "Galletas Marías", 10, 18.00),
                    OrderProduct("p4", "Aceite Vegetal 1L", 6, 45.50)
                )
            ),

            // Pedido 2: Pendiente
            Order(
                id = "order_002",
                date = getDateDaysAgo(1),
                total = 2340.00,
                status = OrderStatus.PENDING,
                products = listOf(
                    OrderProduct("p5", "Arroz 1kg", 15, 22.00),
                    OrderProduct("p6", "Frijol Negro 1kg", 15, 28.00),
                    OrderProduct("p7", "Azúcar 1kg", 20, 18.00),
                    OrderProduct("p8", "Sal 1kg", 10, 8.00),
                    OrderProduct("p9", "Harina de Trigo 1kg", 12, 24.00),
                    OrderProduct("p10", "Café Soluble 200g", 8, 65.00)
                )
            ),

            // Pedido 3: Entregado
            Order(
                id = "order_003",
                date = getDateDaysAgo(5),
                total = 890.00,
                status = OrderStatus.DELIVERED,
                products = listOf(
                    OrderProduct("p11", "Jabón de Tocador", 30, 12.00),
                    OrderProduct("p12", "Shampoo 400ml", 12, 35.00),
                    OrderProduct("p13", "Papel Higiénico 4 rollos", 8, 32.00)
                )
            ),

            // Pedido 4: Cancelado
            Order(
                id = "order_004",
                date = getDateDaysAgo(3),
                total = 560.00,
                status = OrderStatus.CANCELLED,
                products = listOf(
                    OrderProduct("p14", "Atún en Lata", 20, 18.00),
                    OrderProduct("p15", "Sardinas en Lata", 15, 14.00)
                )
            ),

            // Pedido 5: Pendiente
            Order(
                id = "order_005",
                date = getDateHoursAgo(5),
                total = 3150.75,
                status = OrderStatus.PENDING,
                products = listOf(
                    OrderProduct("p16", "Cerveza Corona 355ml", 48, 18.50),
                    OrderProduct("p17", "Refresco Pepsi 2L", 24, 28.00),
                    OrderProduct("p18", "Agua Embotellada 1L", 36, 10.00),
                    OrderProduct("p19", "Jugo Jumex 1L", 18, 22.50)
                )
            ),

            // Pedido 6: Entregado
            Order(
                id = "order_006",
                date = getDateDaysAgo(7),
                total = 1680.00,
                status = OrderStatus.DELIVERED,
                products = listOf(
                    OrderProduct("p20", "Detergente en Polvo 1kg", 12, 45.00),
                    OrderProduct("p21", "Suavizante de Ropa 1L", 10, 38.00),
                    OrderProduct("p22", "Cloro 1L", 15, 22.00),
                    OrderProduct("p23", "Limpiador Multiusos", 8, 35.00)
                )
            )
        ).sortedByDescending { it.date } // Ordenar por fecha (más reciente primero)

        _uiState.value = OrdersUiState(orders = mockOrders)
    }

    /**
     * Obtiene un timestamp de hace X días
     */
    private fun getDateDaysAgo(days: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return calendar.timeInMillis
    }

    /**
     * Obtiene un timestamp de hace X horas
     */
    private fun getDateHoursAgo(hours: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -hours)
        return calendar.timeInMillis
    }
}