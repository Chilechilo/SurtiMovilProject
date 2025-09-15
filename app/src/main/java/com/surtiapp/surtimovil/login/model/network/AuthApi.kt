package com.surtiapp.surtimovil.login.model.network

import com.surtiapp.surtimovil.login.model.LoginRequest
import com.surtiapp.surtimovil.login.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>
}