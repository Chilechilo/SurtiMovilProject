package com.surtiapp.surtimovil.homescreen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surtiapp.surtimovil.addcart.model.CartItem
import com.surtiapp.surtimovil.core.homescreen.repository.HomeRepository
import com.surtiapp.surtimovil.homescreen.model.dto.Category
import com.surtiapp.surtimovil.homescreen.model.dto.Product
import com.surtiapp.surtimovil.homescreen.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ----------------------------------------------------------------------
// HomeUiState
// ----------------------------------------------------------------------
data class HomeUiState(
    val categorias: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)

    fun setUserId(id: String) {
        _userId.value = id
    }

    fun clearMessage() {
        _ui.update { it.copy(message = null, error = null) }
    }

    // --- FUNCIÓN DE CARGA DE DATOS ---
    fun fetchHome() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }

            homeRepository.getHome()
                .onSuccess { homeResponse ->
                    val categoriesList = homeResponse.home ?: emptyList()

                    _ui.update {
                        it.copy(
                            categorias = categoriesList as? List<Category> ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    // ✅ SOLUCIÓN FINAL (LÍNEA 84): Se asegura que el valor es String.
                    _ui.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Error al cargar datos. Verifica tu conexión."
                        )
                    }
                }
        }
    }

    // --- FUNCIÓN DE CARRITO CON FIREBASE ---
    fun addToCart(product: Product) {
        val currentUserId = _userId.value

        if (currentUserId.isNullOrBlank()) {
            _ui.update { it.copy(message = "Error: Usuario no autenticado. Por favor, inicia sesión.") }
            return
        }

        viewModelScope.launch {
            val itemToAdd = CartItem(
                productId = product.id.toString(),
                productName = product.nombre,
                productPrice = product.precio,
                productImageUrl = product.imagen,
                quantity = 1
            )

            _ui.update { it.copy(isLoading = true, message = null) }

            cartRepository.addItemToCart(currentUserId, itemToAdd)
                .onSuccess {
                    _ui.update {
                        it.copy(
                            isLoading = false,
                            message = "¡${product.nombre} agregado con éxito!"
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            isLoading = false,
                            message = "Error al agregar: ${e.message ?: "Intenta de nuevo."}"
                        )
                    }
                }
        }
    }
}