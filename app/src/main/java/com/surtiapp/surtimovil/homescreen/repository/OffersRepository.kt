package com.surtiapp.surtimovil.homescreen.repository

import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.homescreen.model.dto.HomeResponse
import java.io.IOException

class OffersRepository(private val api: HomeApi) {

    suspend fun getOffers(): Result<HomeResponse> {
        return try {
            println("[DEBUG] OffersRepository: Obteniendo ofertas del gist")

            // Usar el método getHome() que ya tiene la ruta correcta configurada
            val resp = api.getHome()

            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    println("[DEBUG] OffersRepository: Ofertas cargadas exitosamente")
                    Result.success(body)
                } else {
                    println("[ERROR] OffersRepository: Respuesta vacía")
                    Result.failure(Exception("Respuesta vacía del servidor (HTTP ${resp.code()})"))
                }
            } else {
                val msg = resp.errorBody()?.string().orEmpty()
                val errorMsg = "HTTP ${resp.code()} - ${resp.message()}\n$msg"
                println("[ERROR] OffersRepository: $errorMsg")
                Result.failure(Exception(errorMsg.ifBlank { "Error al obtener ofertas" }))
            }
        } catch (e: IOException) {
            println("[ERROR] OffersRepository: Sin conexión - ${e.localizedMessage}")
            Result.failure(Exception("Sin conexión. Verifica tu red. (${e.localizedMessage})"))
        } catch (e: Exception) {
            println("[ERROR] OffersRepository: Error inesperado - ${e.localizedMessage}")
            Result.failure(Exception("Error inesperado: ${e.localizedMessage}"))
        }
    }
}