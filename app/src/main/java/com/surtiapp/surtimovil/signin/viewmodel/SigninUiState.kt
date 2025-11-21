package com.surtiapp.surtimovil.signin.viewmodel

data class SignInUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val acceptTerms: Boolean = false,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null
) {
    val passwordsMatch: Boolean get() = password.isNotEmpty() && password == confirm
    val canSubmit: Boolean get() =
        nameError == null &&
                emailError == null &&
                passwordError == null &&
                confirmError == null &&
                name.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirm.isNotBlank() &&
                passwordsMatch &&
                acceptTerms &&
                !isLoading
}