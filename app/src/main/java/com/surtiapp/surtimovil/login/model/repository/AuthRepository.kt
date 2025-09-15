package com.surtiapp.surtimovil.login.model.repository

import com.surtiapp.surtimovil.login.model.LoginRequest
import com.surtiapp.surtimovil.login.model.LoginResponse
import com.surtiapp.surtimovil.login.model.network.AuthApi

class AuthRepository(private val api: AuthApi) {
    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val resp = api.login(LoginRequest(email, password))
            if (resp.isSuccessful) {
                resp.body() ?: LoginResponse(false, "Respuesta vacía del servidor")
            } else {
                val msg = resp.errorBody()?.string().orEmpty()
                val parsed = try {
                    org.json.JSONObject(msg).optString("message", "")
                } catch (_: Exception) { "" }
                LoginResponse(false, parsed.ifBlank { "Credenciales inválidas" })
            }
        } catch (_: java.io.IOException) {
            LoginResponse(false, "Sin conexión. Verifica tu red.")
        } catch (_: Exception) {
            LoginResponse(false, "Error inesperado")
        }
    }
}