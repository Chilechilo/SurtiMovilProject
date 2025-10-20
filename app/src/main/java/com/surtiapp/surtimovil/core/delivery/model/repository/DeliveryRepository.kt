package com.surtiapp.surtimovil.core.delivery.model.repository

import com.surtiapp.surtimovil.core.delivery.model.network.DeliveryApi
import com.surtiapp.surtimovil.core.delivery.model.network.DeliveryConfirmationRequest

class DeliveryRepository(private val api: DeliveryApi) {
    suspend fun confirmDelivery(orderId: String) =
        api.confirmDelivery(DeliveryConfirmationRequest(orderId))
}