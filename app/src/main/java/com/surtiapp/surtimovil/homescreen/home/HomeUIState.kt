package com.surtiapp.surtimovil.homescreen.home

import com.surtiapp.surtimovil.homescreen.model.dto.Category

data class HomeUiState(
    val categorias: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)