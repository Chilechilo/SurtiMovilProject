package com.surtiapp.surtimovil.Signin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.surtiapp.surtimovil.login.model.repository.AuthRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignInViewModel(
    private val repo: AuthRepository,
    app: Application
) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(SignInUiState())
    val ui: StateFlow<SignInUiState> = _ui.asStateFlow()

    // Eventos one-shot
    private val _toastEvents = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    private val _signUpSuccessEvents = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val signUpSuccessEvents: SharedFlow<Unit> = _signUpSuccessEvents.asSharedFlow()

    // --- Handlers de UI ---
    fun onNameChange(v: String) {
        _ui.update { it.copy(name = v).validateName() }
    }

    fun onEmailChange(v: String) {
        _ui.update { it.copy(email = v).validateEmail() }
    }

    fun onPasswordChange(v: String) {
        _ui.update { it.copy(password = v).validatePassword().validateConfirm() }
    }

    fun onConfirmChange(v: String) {
        _ui.update { it.copy(confirm = v).validateConfirm() }
    }

    fun onAcceptTermsChange(v: Boolean) {
        _ui.update { it.copy(acceptTerms = v) }
    }

    // --- Acción principal ---
    fun signUp() {
        val current = _ui.value
        val validated = current
            .validateName()
            .validateEmail()
            .validatePassword()
            .validateConfirm()

        _ui.value = validated

        if (!validated.canSubmit) {
            viewModelScope.launch {
                _toastEvents.emit(firstBlockingReason(validated) ?: "Revisa los datos del formulario")
            }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true) }
            val result = runCatching {
                // Ajusta a tu repo/DTO si es necesario
                repo.signUp(validated.name.trim(), validated.email.trim(), validated.password)
            }.fold(
                onSuccess = { it },
                onFailure = { Result.failure(it) }
            )

            if (result.isSuccess) {
                _toastEvents.emit("Cuenta creada correctamente")
                _signUpSuccessEvents.emit(Unit)
            } else {
                val msg = result.exceptionOrNull()?.message ?: "No se pudo crear la cuenta"
                _toastEvents.emit(msg)
            }
            _ui.update { it.copy(isLoading = false) }
        }
    }

    // --- Helpers de validación ---
    private fun SignInUiState.validateName(): SignInUiState {
        val err = if (name.isBlank()) "Ingresa tu nombre" else null
        return copy(nameError = err)
    }

    private fun SignInUiState.validateEmail(): SignInUiState {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val err = when {
            email.isBlank() -> "Ingresa tu correo"
            !emailRegex.matches(email.trim()) -> "Correo inválido"
            else -> null
        }
        return copy(emailError = err)
    }

    private fun SignInUiState.validatePassword(): SignInUiState {
        val err = when {
            password.isBlank() -> "Ingresa una contraseña"
            password.length < 8 -> "Mínimo 8 caracteres"
            else -> null
        }
        return copy(passwordError = err)
    }

    private fun SignInUiState.validateConfirm(): SignInUiState {
        val err = when {
            confirm.isBlank() -> "Confirma tu contraseña"
            password != confirm -> "Las contraseñas no coinciden"
            else -> null
        }
        return copy(confirmError = err)
    }

    private fun firstBlockingReason(s: SignInUiState): String? = when {
        s.nameError != null -> s.nameError
        s.emailError != null -> s.emailError
        s.passwordError != null -> s.passwordError
        s.confirmError != null -> s.confirmError
        !s.acceptTerms        -> "Debes aceptar los términos"
        else -> null
    }
}