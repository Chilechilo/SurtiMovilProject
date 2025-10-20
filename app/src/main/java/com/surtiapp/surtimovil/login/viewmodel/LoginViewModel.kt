package com.surtiapp.surtimovil.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.login.model.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: AuthRepository,
    application: Application
) : AndroidViewModel(application) {

    private val app = getApplication<Application>()

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    private val _toastEvents = Channel<String>(Channel.BUFFERED)
    val toastEvents = _toastEvents.receiveAsFlow()

    // Evento de inicio de sesión exitoso
    private val _loginSuccessEvents = Channel<Unit>(Channel.BUFFERED)
    val loginSuccessEvents = _loginSuccessEvents.receiveAsFlow()

    fun onEmailChange(v: String) { _ui.value = _ui.value.copy(email = v) }
    fun onPasswordChange(v: String) { _ui.value = _ui.value.copy(password = v) }

    fun login() {
        val email = _ui.value.email.trim()
        val password = _ui.value.password
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _toastEvents.send(app.getString(R.string.login_required_fields))
            }
            return
        }

        _ui.value = _ui.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val res = repo.login(email, password)
                if (res.success) {
                    _toastEvents.send(
                        app.getString(R.string.login_success, res.user?.name ?: "")
                    )
                    _loginSuccessEvents.send(Unit) // ⟵ dispara navegación
                } else {
                    _toastEvents.send(
                        res.message.ifBlank { app.getString(R.string.login_failed) }
                    )
                }
            } catch (e: Exception) {
                _toastEvents.send(app.getString(R.string.login_network_error))
            } finally {
                _ui.value = _ui.value.copy(isLoading = false)
            }
        }
    }
    fun loginWithBiometric() {
        // Aquí puedes implementar lógica para auto-login,
        // por ejemplo usando DataStore o SharedPreferences para recuperar token guardado
        viewModelScope.launch {
            // Simulación de éxito
            _loginSuccessEvents.send(Unit)
        }
    }
}
