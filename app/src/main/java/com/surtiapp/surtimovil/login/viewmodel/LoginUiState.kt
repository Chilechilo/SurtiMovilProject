package com.surtiapp.surtimovil.login.viewmodel

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val authToken: String? = null
)