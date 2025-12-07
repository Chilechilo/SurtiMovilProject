package com.surtiapp.surtimovil.homescreen.repository

import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.homescreen.model.dto.CategoryDto
import com.surtiapp.surtimovil.homescreen.model.dto.ProductDto

class OffersRepository(
    private val api: HomeApi
) {

    suspend fun getProducts(): Result<List<ProductDto>> = try {
        val resp = api.getAllProducts()

        if (resp.isSuccessful && resp.body()?.success == true) {
            Result.success(resp.body()!!.products)
        } else {
            Result.failure(Exception("Error al obtener productos"))
        }

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCategories(): Result<List<CategoryDto>> = try {
        val resp = api.getCategories()

        if (resp.isSuccessful && resp.body()?.success == true) {
            Result.success(resp.body()!!.categories)
        } else {
            Result.failure(Exception("Error al obtener categorías"))
        }

    } catch (e: Exception) {
        Result.failure(e)
    }
}
