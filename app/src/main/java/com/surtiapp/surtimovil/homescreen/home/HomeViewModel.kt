package com.surtiapp.surtimovil.homescreen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surtiapp.surtimovil.addcart.model.CartItem
import com.surtiapp.surtimovil.core.homescreen.repository.HomeRepository
import com.surtiapp.surtimovil.homescreen.model.dto.CategoryDto
import com.surtiapp.surtimovil.homescreen.model.dto.ProductDto
import com.surtiapp.surtimovil.homescreen.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val categorias: List<CategoryDto> = emptyList(),
    val productos: List<ProductDto> = emptyList(),
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

    fun fetchHomeData() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }

            val categoriesResult = homeRepository.getCategories()
            val productsResult = homeRepository.getAllProducts()

            if (categoriesResult.isSuccess && productsResult.isSuccess) {
                val categorias = categoriesResult.getOrDefault(emptyList())
                val productos = productsResult.getOrDefault(emptyList())

                _ui.update {
                    it.copy(
                        categorias = categorias,
                        productos = productos,
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                val msg = categoriesResult.exceptionOrNull()?.message
                    ?: productsResult.exceptionOrNull()?.message
                    ?: "Error desconocido"

                _ui.update {
                    it.copy(
                        isLoading = false,
                        error = msg
                    )
                }
            }
        }
    }

    fun addToCart(product: ProductDto) {
        val currentUserId = _userId.value

        if (currentUserId.isNullOrBlank()) {
            _ui.update { it.copy(message = "Debes iniciar sesión para agregar productos.") }
            return
        }

        viewModelScope.launch {
            val itemToAdd = CartItem(
                productId = product.id.toString(),
                productName = product.name,
                productPrice = product.price,
                productImageUrl = product.image,
                quantity = 1
            )

            cartRepository.addItemToCart(currentUserId, itemToAdd)
                .onSuccess {
                    _ui.update {
                        it.copy(message = "Agregado: ${product.name}")
                    }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(message = "Error al agregar: ${e.message}")
                    }
                }
        }
    }
}
