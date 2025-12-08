package com.surtiapp.surtimovil.login.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: User? = null
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)
