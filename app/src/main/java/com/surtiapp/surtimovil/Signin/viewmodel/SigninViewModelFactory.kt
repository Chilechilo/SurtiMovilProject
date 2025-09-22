package com.surtiapp.surtimovil.Signin.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.surtiapp.surtimovil.login.model.repository.AuthRepository

class SignInViewModelFactory(
    private val repo: AuthRepository,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignInViewModel(repo, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}