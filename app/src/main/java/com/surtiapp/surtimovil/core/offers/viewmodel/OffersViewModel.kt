package com.surtiapp.surtimovil.core.offers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surtiapp.surtimovil.homescreen.model.dto.Product
import com.surtiapp.surtimovil.homescreen.model.dto.Category
import com.surtiapp.surtimovil.homescreen.repository.OffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Clase para asociar producto con su categoría
data class ProductWithCategory(
    val product: Product,
    val category: String
)

data class OffersUiState(
    val allProducts: List<ProductWithCategory> = emptyList(),
    val filteredProducts: List<ProductWithCategory> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "Todas",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class OffersViewModel(
    private val repository: OffersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OffersUiState())
    val uiState: StateFlow<OffersUiState> = _uiState.asStateFlow()

    init {
        fetchOffers()
    }

    fun fetchOffers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getOffers()
                .onSuccess { response ->
                    // Crear lista de productos con su categoría asociada
                    val productsWithCategory = response.home.flatMap { category ->
                        category.productos.map { product ->
                            ProductWithCategory(
                                product = product,
                                category = category.categoria
                            )
                        }
                    }

                    // Extraer categorías únicas
                    val categories = listOf("Todas") + response.home.map { it.categoria }.distinct().sorted()

                    _uiState.update {
                        it.copy(
                            allProducts = productsWithCategory,
                            filteredProducts = productsWithCategory,
                            categories = categories,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Error al cargar ofertas"
                        )
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val filtered = currentState.allProducts.filter { productWithCategory ->
            val matchesSearch = productWithCategory.product.nombre.contains(
                currentState.searchQuery,
                ignoreCase = true
            )
            val matchesCategory = currentState.selectedCategory == "Todas" ||
                    productWithCategory.category == currentState.selectedCategory
            matchesSearch && matchesCategory
        }

        _uiState.update { it.copy(filteredProducts = filtered) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}