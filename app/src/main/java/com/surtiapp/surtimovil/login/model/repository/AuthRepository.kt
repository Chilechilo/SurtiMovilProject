package com.surtiapp.surtimovil.login.model.repository

import android.app.Application
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.login.model.LoginRequest
import com.surtiapp.surtimovil.login.model.LoginResponse
import com.surtiapp.surtimovil.login.model.SignUpRequest
import com.surtiapp.surtimovil.login.model.network.AuthApi
import org.json.JSONObject
import java.io.IOException

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
                    JSONObject(raw).optString("message", "")
                } catch (_: Exception) { "" }
                LoginResponse(false, parsed.ifBlank { app.getString(R.string.auth_invalid_credentials) })
            }
        } catch (_: IOException) {
            LoginResponse(false, app.getString(R.string.auth_offline))
        } catch (_: Exception) {
            LoginResponse(false, app.getString(R.string.auth_unexpected_error))
        }
    }

    suspend fun signUp(name: String, email: String, password: String): Result<Unit> {
        return try {
            val req = SignUpRequest(
                name = name,
                email = email,
                password = password
            )

            val resp = api.signUp(req)

            if (resp.isSuccessful) {
                val body = resp.body()
                if (body?.success == true) {
                    Result.success(Unit)
                } else {
                    val msg = body?.message ?: app.getString(R.string.auth_unexpected_error)
                    Result.failure(Exception(msg))
                }
            } else {
                val raw = resp.errorBody()?.string().orEmpty()
                val parsedMsg = try {
                    JSONObject(raw).optString("message", "")
                } catch (_: Exception) { "" }
                Result.failure(
                    Exception(
                        parsedMsg.ifBlank { app.getString(R.string.auth_unexpected_error) }
                    )
                )
            }
        } catch (_: IOException) {
            Result.failure(Exception(app.getString(R.string.auth_offline)))
        } catch (e: Exception) {
            Result.failure(Exception(app.getString(R.string.auth_unexpected_error)))
        }
    }
}
