package com.surtiapp.surtimovil.core.orders.model.network

import com.surtiapp.surtimovil.addcart.model.Producto
import com.surtiapp.surtimovil.core.orders.model.Order
import com.surtiapp.surtimovil.core.orders.model.OrderProduct
import com.surtiapp.surtimovil.core.orders.model.OrderStatus
import com.surtiapp.surtimovil.login.model.network.RetrofitProvider
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrdersRepository {

    private val api: OrdersApi =
        RetrofitProvider.retrofit.create(OrdersApi::class.java)

    // ---------- Helpers de mapeo ----------

    private fun String?.toOrderStatus(): OrderStatus =
        when (this?.lowercase(Locale.ROOT)) {
            "delivered" -> OrderStatus.DELIVERED
            "canceled", "cancelled" -> OrderStatus.CANCELLED
            else -> OrderStatus.PENDING
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

    private fun mapDtoToOrder(dto: OrderDto): Order {
        val millis = parseIsoToMillis(dto.createdAt)

        val products = dto.items.map { item ->
            OrderProduct(
                id = item.productId.toString(),   // Int -> String para el modelo local
                name = item.name,
                quantity = item.quantity,
                unitPrice = item.price
            )
        }

        return Order(
            id = dto.id,
            orderNumber = dto.orderNumber,
            date = millis,
            total = dto.total,
            status = dto.status.toOrderStatus(),
            products = products
        )
    }

    // ---------- Llamadas públicas ----------

    /** GET /api/orders/my */
    suspend fun getMyOrders(token: String): Result<List<Order>> {
        return try {
            val resp = api.getMyOrders(authHeader = "Bearer $token")

            if (!resp.isSuccessful) {
                return Result.failure(Exception("Error HTTP ${resp.code()}"))
            }

            val body = resp.body()
            if (body?.success == true && body.orders != null) {
                Result.success(body.orders.map { mapDtoToOrder(it) })
            } else {
                Result.failure(
                    Exception(body?.message ?: "Error al obtener pedidos")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** POST /api/orders */
    suspend fun createOrderFromCart(
        token: String,
        productosCarrito: List<Producto>
    ): Result<Order> {

        // 🔥 Extraemos SOLO los dígitos del id que venga del carrito.
        // "1"           -> 1
        // "Electronics_1" -> 1
        // "cat-12"      -> 12
        val itemsReq = productosCarrito.mapNotNull { producto ->
            val rawId = producto.id
            val numericId = rawId.filter { it.isDigit() }      // solo números
            val pid: Int? = numericId.toIntOrNull()
            val quantity = producto.cantidadEnCarrito

            if (pid == null || quantity <= 0) {
                null
            } else {
                CreateOrderItemRequest(
                    productId = pid,        // Int que espera el backend
                    quantity = quantity
                )
            }
        }

        if (itemsReq.isEmpty()) {
            return Result.failure(
                Exception("No hay productos válidos para crear el pedido")
            )
        }

        val request = CreateOrderRequest(items = itemsReq)

        return try {
            val resp = api.createOrder(
                authHeader = "Bearer $token",
                request = request
            )

            if (!resp.isSuccessful) {
                return Result.failure(Exception("Error HTTP ${resp.code()}"))
            }

            val body = resp.body()
            if (body?.success == true && body.order != null) {
                Result.success(mapDtoToOrder(body.order))
            } else {
                Result.failure(
                    Exception(body?.message ?: "No se pudo crear el pedido")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
