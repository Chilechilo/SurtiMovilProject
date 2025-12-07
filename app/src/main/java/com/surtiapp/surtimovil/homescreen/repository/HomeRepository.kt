package com.surtiapp.surtimovil.core.homescreen.repository

import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.homescreen.model.dto.CategoryDto
import com.surtiapp.surtimovil.homescreen.model.dto.ProductDto
import retrofit2.Response

class HomeRepository(private val api: HomeApi) {

    suspend fun getCategories(): Result<List<CategoryDto>> = try {
        val resp: Response<com.surtiapp.surtimovil.homescreen.model.dto.CategoryResponse> =
            api.getCategories()
        val body = resp.body()

        if (resp.isSuccessful && body != null && body.success) {
            Result.success(body.categories)
        } else {
            Result.failure(Exception("Error al obtener categorías"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getAllProducts(): Result<List<ProductDto>> = try {
        val resp: Response<com.surtiapp.surtimovil.homescreen.model.dto.ProductResponse> =
            api.getAllProducts()
        val body = resp.body()

        if (resp.isSuccessful && body != null && body.success) {
            Result.success(body.products)
        } else {
            Result.failure(Exception("Error al obtener productos"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getProductsByCategory(category: String): Result<List<ProductDto>> = try {
        val resp: Response<com.surtiapp.surtimovil.homescreen.model.dto.ProductResponse> =
            api.getProductsByCategory(category)
        val body = resp.body()

        if (resp.isSuccessful && body != null && body.success) {
            Result.success(body.products)
        } else {
            Result.failure(Exception("Error al obtener productos por categoría"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
