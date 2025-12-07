package com.surtiapp.surtimovil.homescreen.repository

import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.homescreen.model.dto.CategoryDto
import com.surtiapp.surtimovil.homescreen.model.dto.CategoryResponse
import com.surtiapp.surtimovil.homescreen.model.dto.ProductDto
import com.surtiapp.surtimovil.homescreen.model.dto.ProductResponse
import retrofit2.Response

class OffersRepository(
    private val api: HomeApi
) {

    suspend fun getProducts(): Result<List<ProductDto>> = try {
        val resp: Response<ProductResponse> = api.getAllProducts()
        val body = resp.body()

        if (resp.isSuccessful && body != null && body.success) {
            Result.success(body.products)
        } else {
            Result.failure(Exception("Error al obtener productos"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCategories(): Result<List<CategoryDto>> = try {
        val resp: Response<CategoryResponse> = api.getCategories()
        val body = resp.body()

        if (resp.isSuccessful && body != null && body.success) {
            Result.success(body.categories)
        } else {
            Result.failure(Exception("Error al obtener categorías"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
