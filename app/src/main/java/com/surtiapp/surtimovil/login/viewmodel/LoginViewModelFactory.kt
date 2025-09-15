package com.surtiapp.surtimovil.login.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.surtiapp.surtimovil.login.model.repository.AuthRepository

class LoginViewModelFactory(
    private val repo: AuthRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repo, application) as T
    }
}
