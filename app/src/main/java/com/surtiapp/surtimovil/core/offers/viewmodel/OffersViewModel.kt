package com.surtiapp.surtimovil.core.offers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surtiapp.surtimovil.homescreen.model.dto.ProductDto
import com.surtiapp.surtimovil.homescreen.model.dto.CategoryDto
import com.surtiapp.surtimovil.homescreen.repository.OffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Relación producto + categoría
data class ProductWithCategory(
    val product: ProductDto,
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

            val productsResult = repository.getProducts()
            val categoriesResult = repository.getCategories()

            if (productsResult.isSuccess && categoriesResult.isSuccess) {

                val products = productsResult.getOrDefault(emptyList<ProductDto>())
                val categoriesDto = categoriesResult.getOrDefault(emptyList<CategoryDto>())

                // Convertimos categorías a solo string
                val categories = listOf("Todas") + categoriesDto
                    .map { it.category }
                    .sorted()

                // Relación producto → categoría
                val productWithCategoryList = products.map { product ->
                    ProductWithCategory(
                        product = product,
                        category = product.category
                    )
                }

                _uiState.update {
                    it.copy(
                        allProducts = productWithCategoryList,
                        filteredProducts = productWithCategoryList,
                        categories = categories,
                        isLoading = false,
                        error = null
                    )
                }

            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al obtener ofertas"
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
        val current = _uiState.value

        val filtered = current.allProducts.filter { item ->

            val matchesSearch = item.product.name.contains(
                current.searchQuery,
                ignoreCase = true
            )

            val matchesCategory =
                current.selectedCategory == "Todas" ||
                        item.category == current.selectedCategory

            matchesSearch && matchesCategory
        }

        _uiState.update { it.copy(filteredProducts = filtered) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
