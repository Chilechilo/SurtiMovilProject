package com.surtiapp.surtimovil.core.homescreen.repository

import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.homescreen.model.dto.HomeResponse
import java.io.IOException

class HomeRepository(private val api: HomeApi) {
    // La lógica de la URL y manejo de errores es correcta, solo se ajusta el package
    suspend fun getHome(): Result<HomeResponse> {
        return try {
            val url = "https://gist.githubusercontent.com/c8b664fc2a7c89474ea5a9393c0e53a4/raw/8d445be823e3e2265a82291347604cb0d5a02691/gistfile1.json"
            println("[DEBUG] HomeRepository: URL de petición: $url")
            val resp = api.getHome()
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor (HTTP ${resp.code()})"))
                }
            } else {
                val msg = resp.errorBody()?.string().orEmpty()
                val errorMsg = "HTTP ${resp.code()} - ${resp.message()}\n$msg"
                Result.failure(Exception(errorMsg.ifBlank { "Error al obtener datos de Home" }))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión. Verifica tu red. (${e.localizedMessage})"))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.localizedMessage}"))
        }
    }
}