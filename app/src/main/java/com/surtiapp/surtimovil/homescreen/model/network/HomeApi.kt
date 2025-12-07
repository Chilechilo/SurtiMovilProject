package com.surtiapp.surtimovil.core.homescreen.model.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.surtiapp.surtimovil.homescreen.model.dto.*

interface HomeApi {

    @GET("categories")
    suspend fun getCategories(): Response<CategoryResponse>

    @GET("products")
    suspend fun getAllProducts(): Response<ProductResponse>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): Response<ProductResponse>
}