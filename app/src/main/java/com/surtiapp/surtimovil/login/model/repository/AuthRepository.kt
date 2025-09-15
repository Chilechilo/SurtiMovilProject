package com.surtiapp.surtimovil.login.model.repository

import android.app.Application
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.login.model.LoginRequest
import com.surtiapp.surtimovil.login.model.LoginResponse
import com.surtiapp.surtimovil.login.model.network.AuthApi

class AuthRepository(
    private val api: AuthApi,
    private val app: Application
) {
    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val resp = api.login(LoginRequest(email, password))
            if (resp.isSuccessful) {
                resp.body() ?: LoginResponse(false, app.getString(R.string.auth_empty_response))
            } else {
                val raw = resp.errorBody()?.string().orEmpty()
                val parsed = try {
                    org.json.JSONObject(raw).optString("message", "")
                } catch (_: Exception) { "" }
                LoginResponse(false, parsed.ifBlank { app.getString(R.string.auth_invalid_credentials) })
            }
        } catch (_: java.io.IOException) {
            LoginResponse(false, app.getString(R.string.auth_offline))
        } catch (_: Exception) {
            LoginResponse(false, app.getString(R.string.auth_unexpected_error))
        }
    }
}
