package com.surtiapp.surtimovil.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.surtiapp.surtimovil.login.model.repository.AuthRepository

class LoginViewModelFactory(
    private val repo: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repo) as T
    }
}