package com.surtiapp.surtimovil.core.orders.model.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// ----- REQUESTS -----

data class CreateOrderItemRequest(
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity")  val quantity: Int
)

data class CreateOrderRequest(
    @SerializedName("items") val items: List<CreateOrderItemRequest>
)

// ----- RESPONSES -----

data class OrderItemDto(
    @SerializedName("productId") val productId: Int,   // ← INT (como en el backend)
    @SerializedName("name")      val name: String,
    @SerializedName("price")     val price: Double,
    @SerializedName("quantity")  val quantity: Int,
    @SerializedName("subtotal")  val subtotal: Double
)

data class OrderDto(
    @SerializedName("_id")         val id: String,
    @SerializedName("orderNumber") val orderNumber: Int,
    @SerializedName("items")       val items: List<OrderItemDto>,
    @SerializedName("total")       val total: Double,
    @SerializedName("status")      val status: String,
    @SerializedName("createdAt")   val createdAt: String?
)

data class CreateOrderResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("order")   val order: OrderDto?
)

data class MyOrdersResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("orders")  val orders: List<OrderDto>?
)

// ----- ENDPOINTS -----

interface OrdersApi {

    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") authHeader: String,
        @Body request: CreateOrderRequest
    ): Response<CreateOrderResponse>

    @GET("orders/my")
    suspend fun getMyOrders(
        @Header("Authorization") authHeader: String
    ): Response<MyOrdersResponse>
}
