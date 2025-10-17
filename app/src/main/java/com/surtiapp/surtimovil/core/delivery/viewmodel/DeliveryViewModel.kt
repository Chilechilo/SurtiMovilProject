package com.surtiapp.surtimovil.core.delivery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DeliveryUiState(
    val loading: Boolean = false,
    val success: Boolean? = null,
    val message: String? = null
)

class DeliveryViewModel : ViewModel() {
    private val _ui = MutableStateFlow(DeliveryUiState())
    val ui = _ui.asStateFlow()

    fun confirmDelivery(orderId: String) {
        viewModelScope.launch {
            _ui.value = DeliveryUiState(loading = true)
            delay(2000) // simulate network delay

            // Simulated backend logic
            if (orderId.startsWith("order_")) {
                _ui.value = DeliveryUiState(
                    loading = false,
                    success = true,
                    message = "Delivery for $orderId confirmed successfully."
                )
            } else {
                _ui.value = DeliveryUiState(
                    loading = false,
                    success = false,
                    message = "Invalid QR code or unknown order."
                )
            }
        }
    }
}