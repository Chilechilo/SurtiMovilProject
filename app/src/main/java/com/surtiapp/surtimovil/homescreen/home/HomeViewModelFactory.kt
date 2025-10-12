package com.surtiapp.surtimovil.homescreen.home.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras // <-- IMPORTANTE: Este import es clave
import com.surtiapp.surtimovil.core.homescreen.repository.HomeRepository
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel

class HomeViewModelFactory(
    private val repo: HomeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // La versión simple, sin usar CreationExtras, sigue siendo válida si regresas el ViewModel
        return HomeViewModel(repo) as T
    }

    // Nota: El método obsoleto 'create(modelClass: Class<T>)' no debe usarse
}
