package com.surtiapp.surtimovil.core.delivery.model.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class DeliveryConfirmationRequest(
    val orderId: String
)

data class DeliveryConfirmationResponse(
    val success: Boolean,
    val message: String
)

interface DeliveryApi {
    @POST("api/orders/confirm-delivery")
    suspend fun confirmDelivery(
        @Body request: DeliveryConfirmationRequest
    ): Response<DeliveryConfirmationResponse>
}