package com.surtiapp.surtimovil.core.orders.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surtiapp.surtimovil.addcart.model.Producto
import com.surtiapp.surtimovil.core.orders.model.Order
import com.surtiapp.surtimovil.core.orders.model.OrderProduct
import com.surtiapp.surtimovil.core.orders.model.OrderStatus
import com.surtiapp.surtimovil.core.orders.model.network.CreateOrderItemRequest
import com.surtiapp.surtimovil.core.orders.model.network.CreateOrderRequest
import com.surtiapp.surtimovil.core.orders.model.network.OrderDto
import com.surtiapp.surtimovil.core.orders.model.network.OrdersApi
import com.surtiapp.surtimovil.login.model.network.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class OrdersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState = _uiState.asStateFlow()

    private val api: OrdersApi =
        RetrofitProvider.retrofit.create(OrdersApi::class.java)

    init {
        _uiState.value = OrdersUiState(orders = emptyList())
    }

    // ---------- CREAR PEDIDO DESDE CARRITO ----------

    suspend fun createOrderFromCart(
        productosCarrito: List<Producto>,
        token: String
    ): Pair<Boolean, String?> {
        if (productosCarrito.isEmpty()) {
            return false to "El carrito está vacío"
        }

        val bearer = "Bearer $token"

        val itemsReq = productosCarrito.mapNotNull { prod ->
            val rawId = prod.id
            val numericId = rawId.filter { it.isDigit() }
            val pid: Int? = numericId.toIntOrNull()
            val qty = prod.cantidadEnCarrito

            if (pid == null || qty <= 0) {
                null
            } else {
                CreateOrderItemRequest(
                    productId = pid,
                    quantity = qty
                )
            }
        }

        if (itemsReq.isEmpty()) {
            return false to "No hay productos válidos para crear el pedido"
        }

        return try {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            val resp = api.createOrder(
                authHeader = bearer,
                request = CreateOrderRequest(items = itemsReq)
            )

            val body = resp.body()
            if (!resp.isSuccessful || body == null || body.success.not()) {
                val msg = body?.message ?: "Error al crear el pedido"
                _uiState.value = _uiState.value.copy(loading = false, error = msg)
                false to msg
            } else {
                val orderMapped = mapDtoToOrder(body.order!!)
                val current = _uiState.value.orders.toMutableList()
                current.add(0, orderMapped)
                _uiState.value = OrdersUiState(
                    orders = current,
                    loading = false,
                    error = null
                )
                true to null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val msg = "No se pudo crear el pedido"
            _uiState.value = _uiState.value.copy(loading = false, error = msg)
            false to msg
        }
    }


    // ---------- CARGAR PEDIDOS DEL USUARIO ----------

    fun fetchMyOrders(token: String) {
        if (token.isBlank()) return

        val bearer = "Bearer $token"

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true, error = null)

                val resp = api.getMyOrders(bearer)
                val body = resp.body()

                if (!resp.isSuccessful || body == null || body.success.not()) {
                    val raw = resp.errorBody()?.string().orEmpty()
                    Log.e("OrdersViewModel", "getMyOrders errorBody: $raw")

                    val parsedMsg = try {
                        JSONObject(raw).optString("message", "")
                    } catch (_: Exception) { "" }

                    val msg = parsedMsg.ifBlank { body?.message ?: "Error al obtener pedidos" }

                    _uiState.value = _uiState.value.copy(loading = false, error = msg)
                } else {
                    val mapped = body.orders.orEmpty().map { mapDtoToOrder(it) }
                    _uiState.value = OrdersUiState(
                        orders = mapped,
                        loading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = "No se pudieron cargar los pedidos"
                _uiState.value = _uiState.value.copy(loading = false, error = msg)
            }
        }
    }

    // ---------- RECOMENDACIONES ----------

    fun getRecommendedProducts(): List<RecommendedProduct> {
        val currentOrders = _uiState.value.orders
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

        return productFrequency.values
            .sortedByDescending { it.count }
            .map { freq ->
                RecommendedProduct(
                    id = freq.id,
                    name = freq.name,
                    unitPrice = freq.unitPrice,
                    timesPurchased = freq.count,
                    totalSpent = freq.totalSpent,
                    imageUrl = ""
                )
            }
    }

    // ---------- MAPEO DTO → MODELO LOCAL ----------

    private fun mapDtoToOrder(dto: OrderDto): Order {
        val millis = parseIsoToMillis(dto.createdAt)
        val products = dto.items.map {
            OrderProduct(
                id = it.productId.toString(),   // Int -> String
                name = it.name,
                quantity = it.quantity,
                unitPrice = it.price
            )
        }
        val status = when (dto.status.lowercase(Locale.ROOT)) {
            "delivered" -> OrderStatus.DELIVERED
            "canceled", "cancelled" -> OrderStatus.CANCELLED
            else -> OrderStatus.PENDING
        }
        return Order(
            id = dto.id,
            orderNumber = dto.orderNumber,
            date = millis,
            total = dto.total,
            status = status,
            products = products
        )
    }

    private fun parseIsoToMillis(iso: String?): Long {
        if (iso.isNullOrBlank()) return System.currentTimeMillis()
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(iso)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}

// MODELOS AUXILIARES PARA RECOMENDACIONES
private data class ProductFrequency(
    val id: String,
    val name: String,
    val count: Int,
    val unitPrice: Double,
    val totalSpent: Double
)

data class RecommendedProduct(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val timesPurchased: Int,
    val totalSpent: Double,
    val imageUrl: String
)
