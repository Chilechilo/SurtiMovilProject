package com.surtiapp.surtimovil.core.homescreen.repository
import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi

class HomeRepository(private val api: HomeApi) {

    suspend fun getCategories() = try {
        val resp = api.getCategories()
        if (resp.isSuccessful && resp.body()?.success == true) {
            Result.success(resp.body()!!.categories)
        } else {
            Result.failure(Exception("Error al obtener categorías"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getAllProducts() = try {
        val resp = api.getAllProducts()
        if (resp.isSuccessful && resp.body()?.success == true) {
            Result.success(resp.body()!!.products)
        } else {
            Result.failure(Exception("Error al obtener productos"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getProductsByCategory(category: String) = try {
        val resp = api.getProductsByCategory(category)
        if (resp.isSuccessful && resp.body()?.success == true) {
            Result.success(resp.body()!!.products)
        } else {
            Result.failure(Exception("Error al obtener productos por categoría"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
